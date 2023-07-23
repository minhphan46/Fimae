package com.example.fimae.repository;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.fimae.models.Conversation;
import com.example.fimae.models.Message;
import com.example.fimae.models.Participant;
import com.example.fimae.service.FirebaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ChatRepository {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static ChatRepository instance;
    CollectionReference conversationsRef = firestore.collection("conversations");

    private ChatRepository() {
    }

    public static synchronized ChatRepository getInstance() {
        if (instance == null) {
            instance = new ChatRepository();
        }
        return instance;
    }

    public ListenerRegistration getConversationsRef(@NotNull EventListener<QuerySnapshot> listener) {

        return conversationsRef.addSnapshotListener(listener);
    }

    public Query getConversationQuery() {
        return conversationsRef.whereArrayContains("participantIds", Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
    }

    public Task<Conversation> getOrCreateFriendConversation(String id) {
        ArrayList<String> arrayList = new ArrayList() {{
            add(id);
            add(FirebaseAuth.getInstance().getUid());
        }};
        return getOrCreateConversation(arrayList, Conversation.FRIEND_CHAT);
    }

    public Task<Conversation> getOrCreateConversation(ArrayList<String> participantIds, String type) {

        TaskCompletionSource<Conversation> taskCompletionSource = new TaskCompletionSource<>();
        Collections.sort(participantIds);
        Task<QuerySnapshot> queryTask = conversationsRef
                .whereEqualTo("participantIds", participantIds)
                .whereEqualTo("type", type)
                .limit(1)
                .get();
        queryTask.addOnCompleteListener(querySnapshotTask -> {
            if (querySnapshotTask.isSuccessful()) {
                QuerySnapshot querySnapshot = querySnapshotTask.getResult();
                System.out.println("Res: " + querySnapshot.getDocuments());
                if (!querySnapshot.isEmpty()) {
                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                    Conversation conversation = documentSnapshot.toObject(Conversation.class);
                    assert conversation != null;
                    taskCompletionSource.setResult(conversation);
                } else {
                    DocumentReference newConDoc = conversationsRef.document();
                    WriteBatch batch = FirebaseFirestore.getInstance().batch();
                    CollectionReference collectionReference = newConDoc.collection("participants");
                    Conversation conversation = Conversation.create(newConDoc.getId(), Conversation.FRIEND_CHAT, participantIds);
                    batch.set(newConDoc, conversation);
                    for (String id : participantIds) {
                        Participant participant = Participant.create(id, Participant.ROLE_Participant);
                        batch.set(collectionReference.document(id), participant);
                    }
                    batch.commit().addOnCompleteListener(batchTask -> {
                        if (batchTask.isSuccessful()) {
                            conversation.setId(newConDoc.getId());
                            taskCompletionSource.setResult(conversation);
                        } else {
                            taskCompletionSource.setException(Objects.requireNonNull(batchTask.getException()));
                        }
                    });
                }
            } else {
                taskCompletionSource.setException(Objects.requireNonNull(querySnapshotTask.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }

    public Task<Message> sendTextMessage(String conversationId, String content) {
        TaskCompletionSource<Message> taskCompletionSource = new TaskCompletionSource<Message>();
        if (content.isEmpty()) {
            taskCompletionSource.setException(new Exception("Text message has null content"));
            return taskCompletionSource.getTask();
        }
        WriteBatch batch = firestore.batch();
        DocumentReference currentConversationRef = conversationsRef.document(conversationId);
        CollectionReference reference = currentConversationRef.collection("messages");
        DocumentReference messDoc = reference.document();
        Message message = Message.text(messDoc.getId(), conversationId, content);
        batch.set(messDoc, message);
        batch.update(currentConversationRef, "lastMessage", messDoc);
        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                taskCompletionSource.setResult(message);
            } else {
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }

    public Task<Boolean> updateReadLastMessageAt(String conversationId, Date timeStamp) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        DocumentReference participantRef = conversationsRef.document(conversationId).collection("participants").document(FirebaseAuth.getInstance().getUid());
        participantRef.update("readLastMessageAt", timeStamp).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                taskCompletionSource.setResult(true);
            } else {
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }

    public Task<Participant> getParticipantInConversation(String conversationId, String participantId) {
        TaskCompletionSource<Participant> taskCompletionSource = new TaskCompletionSource<>();
        DocumentReference participantRef = conversationsRef.document(conversationId).collection("participants").document(participantId);
        participantRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                Participant participant = documentSnapshot.toObject(Participant.class);
                assert participant != null;
                taskCompletionSource.setResult(participant);
            } else {
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }

    public Task<Message> sendPostLink(String conversationId, String content) {
        TaskCompletionSource<Message> taskCompletionSource = new TaskCompletionSource<Message>();
        if (content.isEmpty()) {
            taskCompletionSource.setException(new Exception("Text message has null content"));
            return taskCompletionSource.getTask();
        }
        WriteBatch batch = firestore.batch();
        DocumentReference currentConversationRef = conversationsRef.document(conversationId);
        CollectionReference reference = currentConversationRef.collection("messages");
        DocumentReference messDoc = reference.document();
        Message message = new Message();
        message.setId(messDoc.getId());
        message.setType(Message.POST_LINK);
        message.setContent(content);
        message.setIdSender(FirebaseAuth.getInstance().getUid());
        message.setConversationID(conversationId);
        batch.set(messDoc, message);
        batch.update(currentConversationRef, "lastMessage", messDoc);
        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                taskCompletionSource.setResult(message);
            } else {
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }

    public Task<Message> sendMediaMessage(String conversationId, ArrayList<Uri> uris) {
        TaskCompletionSource<Message> taskCompletionSource = new TaskCompletionSource<Message>();
        if (uris.isEmpty()) {
            taskCompletionSource.setException(new Exception("Media message has null content"));
            return taskCompletionSource.getTask();
        }
        WriteBatch batch = firestore.batch();
        DocumentReference currentConversationRef = conversationsRef.document(conversationId);
        CollectionReference reference = currentConversationRef.collection("messages");
        DocumentReference messDoc = reference.document();

        FirebaseService.getInstance().uploadTaskFiles("conversation-medias/" + conversationId + "/messages/" + messDoc.getId(), uris).whenComplete(new BiConsumer<List<String>, Throwable>() {
            @Override
            public void accept(List<String> strings, Throwable throwable) {
                if (throwable != null) {
                    taskCompletionSource.setException((Exception) throwable);
                } else {
                    Message message = Message.media(messDoc.getId(), conversationId, (ArrayList<String>) strings);
                    batch.set(messDoc, message);
                    batch.update(currentConversationRef, "lastMessage", messDoc);
                    batch.update(messDoc, "content", strings);
                    batch.commit().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            taskCompletionSource.setResult(message);
                        } else {
                            taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
                        }
                    });
                }
            }
        });
        return taskCompletionSource.getTask();
    }
}
