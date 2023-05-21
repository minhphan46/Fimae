package com.example.fimae.models;

import com.stringee.messaging.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Seed {
    public Seed(){
    }
    private String content = "Google Images là một dịch vụ tìm kiếm của Google cho phép người dùng tìm hình ảnh trên các trang web. Tính năng này được hoàn chỉnh vào tháng 12 năm 2001. Những từ khóa để tìm kiếm hình ảnh được dựa t";

    private List<Comment> subComments = new ArrayList<>(
            Arrays.asList(
                    new Comment("Comment con số 1 của user 1", "2","1","1","1",null ),
                    new Comment("Comment con số 2 của user 1", "3","1","2","2",null )
            )
    );
    private List<Comment> comments = new ArrayList<>(
            Arrays.asList(
                    new Comment(content, "1","1","1",null,subComments ),
                    new Comment("Comment số 2 của user 1", "2","1","2",null,new ArrayList<>() ),
                    new Comment("Comment số 3 của user 1","3", "1","3",null,new ArrayList<>() ),
                    new Comment("Comment số 4 của user 1","4", "1","4",null,new ArrayList<>() ))
    );

    public List<Post> postseed(){
        List<Post> postList = new ArrayList<Post>();
        String content = "Google Images là một dịch vụ tìm kiếm của Google cho phép người dùng tìm hình ảnh trên các trang web. Tính năng này được hoàn chỉnh vào tháng 12 năm 2001. Những từ khóa để tìm kiếm hình ảnh được dựa t";
        postList.add(new Post("1", new ArrayList<>(Arrays.asList(
                "https://th.bing.com/th/id/OIP.xquDAp-VAFzm8zT8e1VXJgHaE6?pid=ImgDet&rs=1",
                "https://th.bing.com/th/id/R.e1707c345d5ac10c80a674030e606643?rik=pOsTg5KBoLuNvw&riu=http%3a%2f%2fwww.snut.fr%2fwp-content%2fuploads%2f2015%2f08%2fimage-de-paysage.jpg&ehk=1O5SWKkGpZ8yU%2b%2fAnLXG1v8k6BKxgyiXgHbOWBW1ir0%3d&risl=1&pid=ImgRaw&r=0",
                "https://hips.hearstapps.com/clv.h-cdn.co/assets/17/08/2048x1353/aspen.jpg?resize=480:*"
        )), content, "1"));
        postList.add(new Post("1", new ArrayList<>(Arrays.asList(
                "https://th.bing.com/th/id/OIP.xquDAp-VAFzm8zT8e1VXJgHaE6?pid=ImgDet&rs=1",
                "https://th.bing.com/th/id/R.e1707c345d5ac10c80a674030e606643?rik=pOsTg5KBoLuNvw&riu=http%3a%2f%2fwww.snut.fr%2fwp-content%2fuploads%2f2015%2f08%2fimage-de-paysage.jpg&ehk=1O5SWKkGpZ8yU%2b%2fAnLXG1v8k6BKxgyiXgHbOWBW1ir0%3d&risl=1&pid=ImgRaw&r=0",
                "https://hips.hearstapps.com/clv.h-cdn.co/assets/17/08/2048x1353/aspen.jpg?resize=480:*"
        )), content, "2"));
        postList.add(new Post("1", new ArrayList<>(Arrays.asList(
                "https://th.bing.com/th/id/OIP.xquDAp-VAFzm8zT8e1VXJgHaE6?pid=ImgDet&rs=1",
                "https://th.bing.com/th/id/R.e1707c345d5ac10c80a674030e606643?rik=pOsTg5KBoLuNvw&riu=http%3a%2f%2fwww.snut.fr%2fwp-content%2fuploads%2f2015%2f08%2fimage-de-paysage.jpg&ehk=1O5SWKkGpZ8yU%2b%2fAnLXG1v8k6BKxgyiXgHbOWBW1ir0%3d&risl=1&pid=ImgRaw&r=0",
                "https://hips.hearstapps.com/clv.h-cdn.co/assets/17/08/2048x1353/aspen.jpg?resize=480:*"
        )), content, "3"));

        return  postList;
    }

    public List<Comment> getSeedComment(String postid){
        List<Comment> comments1 = new ArrayList<>();
        for(int i =0; i < comments.size(); i++){
            if(comments.get(i).getPostId().equals(postid)){
                comments1.add(comments.get(i));
            }
        }
        return comments1;
    }
    public UserInfo getUserInfoByID(String id){
        UserInfo[] list = UserInfo.dummy;
        for (int i = 0; i < list.length; i++ ){
            if(list[i].getId().equals(id)){
                return list[i];
            }
        }
        return null;
    }
    public Post getPostbyId(String id){
        for(int i = 0; i < postseed().size(); i++ ){
            if(postseed().get(i).getPostId().equals(id)){
                return postseed().get(i);
            }
        }
        return null;
    }
}
