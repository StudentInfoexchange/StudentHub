package com.example.studenthub.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatUtils {

    static void openChat(Context context, Group group) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("groupInfo", group);
        context.startActivity(intent);
    }

    public static void openChat(final Context context, final String itemId, final String regarding, final String toUid) {
        // Show progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String currentUerUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<String> members = new ArrayList<>();
        members.add(currentUerUid);
        members.add(toUid);

        String usersUid1 = currentUerUid + "@" + toUid;
        String usersUid2 = toUid + "@" + currentUerUid;

        List<String> userUids = Arrays.asList(usersUid1, usersUid2);
        FirebaseFirestore.getInstance().collection("group").
                whereArrayContainsAny("usersUids", userUids)
                .limit(1).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        if (documents != null && !documents.isEmpty()) {
                            DocumentSnapshot document = documents.getDocuments().get(0);
                            String groupId = document.getString("id");
                            Group group = new Group(null, groupId, null, regarding);
                            openChat(context, group);
                            progressDialog.dismiss();

                        } else {
                            //create group
                            DocumentReference ref = FirebaseFirestore.getInstance().collection("group").document();
                            Map<String, Object> groupData = new HashMap<>();
                            groupData.put("usersUids", userUids);
                            groupData.put("members", members);
                            groupData.put("createdAt", new Date());
                            groupData.put("id", ref.getId());
                            groupData.put("itemId", itemId);
                            groupData.put("regarding", regarding);
                            groupData.put("updatedAt", new Date());
                            groupData.put("recentMsg", "Contacted");
                            ref.set(groupData).addOnCompleteListener(task1 -> {
                                progressDialog.dismiss();
                                if (task1.isSuccessful()) {
                                    Group group = new Group(null, ref.getId(), null, regarding);
                                    openChat(context, group);
                                } else {
                                    task1.getException().printStackTrace();
                                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        task.getException().printStackTrace();
                    }
                });
    }

}
