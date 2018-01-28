package com.example.ivan.mrmuzzma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private ArrayList<String> chosen = new ArrayList<>();
    private Context context;

    @Override
    public void add(ChatMessage object) {
        object.left = !object.left;
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public void setChosen(int index) {
        getItem(index).chose = !getItem(index).chose;
    }

    public String[] getAllChosen(){
        chosen = new ArrayList<>();
        for (ChatMessage message : chatMessageList) {
            if (message.chose) chosen.add(message.message);
        }
        String[] answer = new String[chosen.size()];
        for (int i = 0; i < answer.length; i++){
            answer[i] =  chosen.get(i);
        }
        return answer;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.left) {
            if (chatMessageObj.chose) row = inflater.inflate(R.layout.right_c, parent, false);
            else row = inflater.inflate(R.layout.right, parent, false);
        }else{
            if (chatMessageObj.chose) row = inflater.inflate(R.layout.left_c, parent, false);
            else row = inflater.inflate(R.layout.left, parent, false);
        }
        chatText = (TextView) row.findViewById(R.id.msgr);
        chatText.setText(chatMessageObj.message);
        return row;
    }
}
