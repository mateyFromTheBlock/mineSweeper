package com.example.arono.minesweeper.Table;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.arono.minesweeper.R;


public class FragmentListTable extends Fragment {

    private TableLayout tableLayout;
    private TableManager table;

    public FragmentListTable() {

    }

    public void setTable() {

        tableLayout.setColumnStretchable(0, true);

        TableRow row = new TableRow(getActivity().getBaseContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        setLabelColumns(row);

        tableLayout.addView(row);

        setAllScores(row);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_fragment_list_table,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tableLayout = (TableLayout) getActivity().findViewById(R.id.ListTable);
        table = new TableManager(this.getActivity().getApplicationContext());

        table.loadScores();

        setTable();
    }

    @Override
    public void onStart() {
        super.onStart();
       // Toast.makeText(getActivity().getBaseContext(),"OnResume",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
       // Toast.makeText(getActivity().getBaseContext(),"OnResume",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
       // Toast.makeText(getActivity().getBaseContext(),"OnPause",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // Toast.makeText(getActivity().getBaseContext(),"OnDestroy",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


   public void setLabelColumns(TableRow row){
       TextView name = new TextView(getActivity().getBaseContext());
       String str = "Name";
       str = String.format("%10s",str);
       name.setText(str);
       name.setTextSize(30);
       name.setBackgroundColor(Color.DKGRAY);
       row.addView(name);

       TextView time = new TextView(getActivity().getBaseContext());
       str = "Time";
       str = String.format("%-13s",str);
       time.setBackgroundColor(Color.DKGRAY);
       time.setText(str);
       time.setTextSize(30);

       row.addView(time);
   }
    public void setAllScores(TableRow row){
        int i = 0;

        for(Score s : table.getScores()) {
            row = new TableRow(getActivity().getBaseContext());

            TextView tvName = new TextView(getActivity().getBaseContext());
            String name = s.getName();
            String position = ""+(i+1);
            String str = String.format("%-5s %-15s",position,name);

            tvName.setText(str);
            tvName.setTextSize(20);
            if(i % 2 == 0)
                row.setBackgroundColor(Color.DKGRAY);
            else {
                row.setBackgroundColor(Color.DKGRAY);
                row.setAlpha(0.9f);
            }
            row.addView(tvName);

            TextView tvTime = new TextView(getActivity().getBaseContext());
            String time = ""+s.getTime();

            str = String.format("%4s",time);
            tvTime.setText(str);
            tvTime.setTextSize(20);
            row.addView(tvTime);
            i++;
            tableLayout.addView(row);
        }
    }
}
