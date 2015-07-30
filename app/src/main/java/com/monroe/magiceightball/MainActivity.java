package com.monroe.magiceightball;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends ActionBarActivity
{

    private View view;
    public String respond;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void shakerClick(View view)
    {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(20);
        //This works now

        switch (randomInt)
        {
            case 1:
                respond = "It is\ncertain";
                break;
            case 2:
                respond = "It is\ndecidedly so";
                break;
            case 3:
                respond = "Without\na doubt";
                break;
            case 4:
                respond = "Yes\ndefinitely";
                break;
            case 5:
                respond = "You may\nrely on it";
                break;
            case 6:
                respond = "As I\nsee it,\nyes";
                break;
            case 7:
                respond = "Most\nlikely";
                break;
            case 8:
                respond = "Outlook\ngood";
                break;
            case 9:
                respond = "Yes";
                break;
            case 10:
                respond = "Signs point\nto yes";
                break;
            case 11:
                respond = "Reply hazy\ntry again";
                break;
            case 12:
                respond = "Ask again\nlater";
                break;
            case 13:
                respond = "Better not\ntell you now";
                break;
            case 14:
                respond = "Cannot\npredict\nnow";
                break;
            case 15:
                respond = "Concentrate\nand ask\nagain";
                break;
            case 16:
                respond = "Don't\ncount\non it";
                break;
            case 17:
                respond = "My reply\nis no";
                break;
            case 18:
                respond = "My sources\nsay no";
                break;
            case 19:
                respond = "Outlook\nnot so\ngood";
                break;
            case 20:
                respond = "Wait, what?";
                break;
            default:
                respond = "Very\ndoubtful";
        }

        createResponse(respond);

        ContentValues values = new ContentValues();
        values.put(Responses.NAME,
                ((EditText) findViewById(R.id.nameEditText)).getText().toString());

        values.put(Responses.RESPONSE, respond);
        Uri uri = getContentResolver().insert(Responses.CONTENT_URI, values);
        Toast.makeText(getBaseContext(), uri.toString() + " inserted!", Toast.LENGTH_LONG).show();
    }

    public void showAllClick(View view)
    {
        String URL = "content://com.monroe.provider.MagicEightBall/friends";
        Uri friends = Uri.parse(URL);
        Cursor c = getContentResolver().query(friends, null, null, null, "name");
        String result = "Monroe Results:";

        if(!c.moveToFirst())
        {
            Toast.makeText(this, result + " no content yet!", Toast.LENGTH_LONG).show();
        }
        else
        {
            do {
                result = result + "\n" + c.getString(c.getColumnIndex(Responses.NAME)) +
                        " with id " + c.getString(c.getColumnIndex(Responses.ID)) +
                        " prophecy is: " + c.getString(c.getColumnIndex(Responses.RESPONSE));

            } while(c.moveToNext());

            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void createResponse(String respond)
    {
        BottomHalf bottomResponse = (BottomHalf) getSupportFragmentManager().findFragmentById(R.id.fragment2);
        bottomResponse.setResponseBlock(respond);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
