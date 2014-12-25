package com.aadisriram.quicktact;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aadisriram.quicktact.fragments.ExampleFragment;


public class MainActivity extends Activity {

    Button toggleFragmentButton;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleFragmentButton = (Button) findViewById(R.id.toggle_fragment);
        toggleFragmentButton.setOnClickListener(new FragmentToggleClickListener());

        fragmentManager = getFragmentManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class FragmentToggleClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentById(R.id.list1);
            if(fragment.isVisible()) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).hide(fragment);
                transaction.addToBackStack(null);
            } else {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).show(fragment);
            }
            transaction.commit();
        }
    }
}
