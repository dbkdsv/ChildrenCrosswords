package com.dbkudryavtsev.childrencrosswords.Views;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbkudryavtsev.childrencrosswords.Activities.CrosswordActivity;
import com.dbkudryavtsev.childrencrosswords.R;

import java.util.Locale;

public class LevelFragment extends Fragment implements View.OnClickListener {

    private static final String levelCompletionString = "levelCompletion";
    private static final String levelCountString = "levelCount";
    public static final String chosenRectString = "chosenRect";

    public static LevelFragment newInstance(int levelCount, int levelCompletion){
        LevelFragment levelFragment = new LevelFragment();
        Bundle args = new Bundle();
        args.putInt(levelCountString, levelCount);
        args.putInt(levelCompletionString, levelCompletion);
        levelFragment.setArguments(args);
        return levelFragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.choice_element, container, false);
        TextView levelCompletionView = (TextView) layout.findViewById(R.id.level_completion);
        TextView levelCountView = (TextView) layout.findViewById(R.id.level_count);
        levelCompletionView.setText(String.format(Locale.getDefault(),
                "%d", getArguments().getInt(levelCompletionString)));
        levelCountView.setText(String.format(Locale.getDefault(),
                "%d", getArguments().getInt(levelCountString)+1));
        layout.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), CrosswordActivity.class);
        intent.putExtra(chosenRectString, getArguments().getInt(levelCountString));
        v.getContext().startActivity(intent);
    }
}
