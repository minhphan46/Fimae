package com.example.fimae.models;


import com.example.fimae.activities.PostMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Seed {
    private static List<Comment> subComments;
    private static List<Comment> comments;
    private static String content = "Google Images là một dịch vụ tìm kiếm của Google cho phép người dùng tìm hình ảnh trên các trang web. Tính năng này được hoàn chỉnh vào tháng 12 năm 2001. Những từ khóa để tìm kiếm hình ảnh được dựa t";

//    public static List<Post> postList = new ArrayList<Post>(Arrays.asList(
//            new Post("1", new ArrayList<>(Arrays.asList(
//                    "https://th.bing.com/th/id/OIP.xquDAp-VAFzm8zT8e1VXJgHaE6?pid=ImgDet&rs=1",
//                    "https://th.bing.com/th/id/R.e1707c345d5ac10c80a674030e606643?rik=pOsTg5KBoLuNvw&riu=http%3a%2f%2fwww.snut.fr%2fwp-content%2fuploads%2f2015%2f08%2fimage-de-paysage.jpg&ehk=1O5SWKkGpZ8yU%2b%2fAnLXG1v8k6BKxgyiXgHbOWBW1ir0%3d&risl=1&pid=ImgRaw&r=0",
//                    "https://hips.hearstapps.com/clv.h-cdn.co/assets/17/08/2048x1353/aspen.jpg?resize=480:*"
//            )), content, "1", PostMode.PUBLIC),
//            new Post("2", new ArrayList<>(Arrays.asList(
//                    "https://th.bing.com/th/id/OIP.xquDAp-VAFzm8zT8e1VXJgHaE6?pid=ImgDet&rs=1",
//                    "https://th.bing.com/th/id/R.e1707c345d5ac10c80a674030e606643?rik=pOsTg5KBoLuNvw&riu=http%3a%2f%2fwww.snut.fr%2fwp-content%2fuploads%2f2015%2f08%2fimage-de-paysage.jpg&ehk=1O5SWKkGpZ8yU%2b%2fAnLXG1v8k6BKxgyiXgHbOWBW1ir0%3d&risl=1&pid=ImgRaw&r=0",
//                    "https://hips.hearstapps.com/clv.h-cdn.co/assets/17/08/2048x1353/aspen.jpg?resize=480:*"
//            )), content, "2", PostMode.PUBLIC),
//            new Post("3", new ArrayList<>(Arrays.asList(
//                    "https://th.bing.com/th/id/OIP.xquDAp-VAFzm8zT8e1VXJgHaE6?pid=ImgDet&rs=1",
//                    "https://th.bing.com/th/id/R.e1707c345d5ac10c80a674030e606643?rik=pOsTg5KBoLuNvw&riu=http%3a%2f%2fwww.snut.fr%2fwp-content%2fuploads%2f2015%2f08%2fimage-de-paysage.jpg&ehk=1O5SWKkGpZ8yU%2b%2fAnLXG1v8k6BKxgyiXgHbOWBW1ir0%3d&risl=1&pid=ImgRaw&r=0",
//                    "https://hips.hearstapps.com/clv.h-cdn.co/assets/17/08/2048x1353/aspen.jpg?resize=480:*"
//            )), content, "3", PostMode.PUBLIC)
//    ));

    public List<Comment> getSeedComment(String postid){
        List<Comment> comments1 = new ArrayList<>();
        for(int i =0; i < comments.size(); i++){
            if(comments.get(i).getPostId().equals(postid)){
                comments1.add(comments.get(i));
            }
        }
        return comments1;
    }

//    public Post getPostbyId(String id){
//        for(int i = 0; i < postList.size(); i++ ){
//            if(postList.get(i).getPostId().equals(id)){
//                return postList.get(i);
//            }
//        }
//        return null;
//    }
//    public Fimaers getCurrentUser( ){
//        return Fimaers.dummy1[0];
//    }
//    public void addPost(Post post){
//        postList.add(post);
//    }
}
