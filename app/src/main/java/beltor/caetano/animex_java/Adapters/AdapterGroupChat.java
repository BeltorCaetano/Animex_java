package beltor.caetano.animex_java.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


import beltor.caetano.animex_java.Models.ModelGroupChat;
import beltor.caetano.animex_java.R;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.HolderGroupChat>{

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private ArrayList<ModelGroupChat> modelGroupChatsList;

    private FirebaseAuth firebaseAuth;

    public AdapterGroupChat(Context context, ArrayList<ModelGroupChat> modelGroupChatsList) {
        this.context = context;
        this.modelGroupChatsList = modelGroupChatsList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       if (viewType == MSG_TYPE_RIGHT){
           View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_right, parent, false);
           return new HolderGroupChat(view);
       }
       else {
           View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_left, parent, false);
           return new HolderGroupChat(view);
       }
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {

        //get data
        ModelGroupChat model = modelGroupChatsList.get(position);
        String timestamp = model.getTimestamp();
        String message = model.getMessage();//if text message then contain message, if image message then contain url of the image stored in storage
        String senderUid = model.getSender();
        String messageType = model.getType();

        // Convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        if (messageType.equals("text")){
            //text message, hide messageIv, show messageIv
            holder.messageIv.setVisibility(View.GONE);
            holder.messageTv.setVisibility(View.VISIBLE);
            holder.messageTv.setText(message);
        }else {
            holder.messageIv.setVisibility(View.VISIBLE);
            holder.messageTv.setVisibility(View.GONE);
        }

        try {
            Picasso.get().load(message).placeholder(R.drawable.ic_image_black).into(holder.messageIv);
        }catch (Exception e){
            holder.messageIv.setImageResource(R.drawable.ic_image_black);
        }
        holder.timeTv.setText(dateTime);

        setUserName(model, holder);
    }

    private void setUserName(ModelGroupChat model, HolderGroupChat holder) {
        //get sender info from uid in model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(model.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            holder.nameTv.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    @Override
    public int getItemViewType(int position) {
        Log.e("TAG.POSITION", String.valueOf(modelGroupChatsList.get(position).getSender()));
if (modelGroupChatsList.get(position).getSender() != null &&
        modelGroupChatsList.get(position).getSender().equals(firebaseAuth.getUid())){
    if (modelGroupChatsList.get(position).getSender().equals(firebaseAuth.getUid())){
        return MSG_TYPE_RIGHT;
    }
    else {
        return MSG_TYPE_LEFT;
    }
}

return MSG_TYPE_LEFT;
    }

    @Override
    public int getItemCount() {
        return modelGroupChatsList.size();
    }

    class HolderGroupChat extends RecyclerView.ViewHolder{
        private TextView nameTv, messageTv, timeTv;
        private ImageView messageIv;

        public HolderGroupChat(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            messageIv = itemView.findViewById(R.id.messageIv);
        }
    }
}
