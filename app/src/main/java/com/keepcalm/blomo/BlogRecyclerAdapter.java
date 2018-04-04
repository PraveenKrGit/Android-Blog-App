package com.keepcalm.blomo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Keep Calm on 3/26/2018.
 */

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder>{

    public  List<BlogPost> blogPostList;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private PopupWindow popupWindow;


    public BlogRecyclerAdapter(List<BlogPost> blogPostList){

        this.blogPostList = blogPostList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);

        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String blogPostId = blogPostList.get(position).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String title_data = blogPostList.get(position).getTitle_txt();
        holder.setTitleText(title_data);

        String desc_data = blogPostList.get(position).getMessage();
        holder.setDescText(desc_data);

        String image_url= blogPostList.get(position).getImage_url();
        String thumbUri = blogPostList.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUri);

        String user_id = blogPostList.get(position).getUser_id();
        //User data retrived here
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    //getting name and image from firestore collection
                    String username = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");


                    holder.setUserData(username, userImage);

                }else{
                    //error handling
                }
            }
        });

        try{
            long miliseconds = blogPostList.get(position).getTimestamp().getTime();
            // String dataString = DateFormat.format("MM/dd/yyyy", new Date(miliseconds)).toString();
            String dateString = android.text.format.DateFormat.format("dd MMM",new Date(miliseconds)).toString();

            holder.setTime(dateString);
        }catch (Exception e){
            Toast.makeText(context, "Exception :"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

            //RealTime Count Likes
            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if(!documentSnapshots.isEmpty()){

                        int count = documentSnapshots.size();

                        holder.updateLikesCount(count);
                    }else{

                        holder.updateLikesCount(0);
                    }
                }
            });

            //Get Likes
            //RealTime Likes
            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                    if(documentSnapshot.exists()){
                        holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.like_button_red));
                    }else{
                        holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.like_button_grey));
                    }



                }
            });

            //Like Feature
            holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if(!task.getResult().exists()){
                                //putting likes
                                Map<String, Object> likesMap = new HashMap<>();
                                likesMap.put("timestamp", FieldValue.serverTimestamp());
                                //Each post will have subcollection named "Likes'
                                firebaseFirestore.collection("Posts/" +blogPostId+"/Likes").document(currentUserId).set(likesMap);
                            }else{
                                //deleting user id i.e. likes
                                firebaseFirestore.collection("Posts/" +blogPostId+"/Likes").document(currentUserId).delete();
                            }

                        }
                    });

                }
            });

            holder.blogCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Intent commentIntent = new Intent(context, CommentActivity.class);
                    context.startActivity(commentIntent);
                }
            });





    }

    @Override
    public int getItemCount() {
        return blogPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        private TextView titleView;
        private TextView descView;
        private ImageView blogImageView;
        private TextView blogDate;

        private TextView blogUserName;
        private CircleImageView blogUserImage;

        private ImageView blogLikeBtn;
        private TextView blogLikeCount;

        private ImageView blogCommentButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            blogLikeBtn = mView.findViewById(R.id.blog_like_button);
            blogCommentButton=mView.findViewById(R.id.blog_comment_btn);
        }
       //My Method
        public void setTitleText(String titleText){

            titleView = mView.findViewById(R.id.blog_title);
            titleView.setText(titleText);
        }

        public void setDescText(String descText){

            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);

        }

        public void setBlogImage(String downloadUri, String thumbUri){

            blogImageView = mView.findViewById(R.id.blog_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.square);

            Glide.with(context).applyDefaultRequestOptions(requestOptions)
                    .load(downloadUri)
                    .thumbnail(Glide.with(context).load(thumbUri))
                    .into(blogImageView);
        }
        public void setTime(String date){

            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);
        }

        public void setUserData(String name, String image){

            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);

            blogUserName.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.circle_profile3);

            //Glide.with(context).load(image).into(blogUserImage);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);


        }

        //Method For Likes Count
        public void updateLikesCount(int count){
            blogLikeCount = mView.findViewById(R.id.blog_like_count);
            blogLikeCount.setText(count + " Likes");
        }

    }
}
