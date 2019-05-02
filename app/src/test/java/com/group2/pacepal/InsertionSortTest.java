package com.group2.pacepal;

import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.Silent.class)
public class InsertionSortTest {


    @Test
    public void insertSortPlayers(){

        ArrayList  <RemotePlayerTest> playerList= new ArrayList<RemotePlayerTest>(0);

        RemotePlayerTest mockPlayer1 = Mockito.spy( RemotePlayerTest.class);
        RemotePlayerTest mockPlayer2 = Mockito.spy(RemotePlayerTest.class);
        RemotePlayerTest mockPlayer3 = Mockito.spy(RemotePlayerTest.class);

        //A set of example text messages that can be in the chatchannel
        Mockito.when(mockPlayer1.getDistance()).thenReturn(10.00);
        Mockito.when(mockPlayer2.getDistance()).thenReturn(19.00);
        Mockito.when(mockPlayer3.getDistance()).thenReturn(3.00);

        playerList.add(mockPlayer2);
        playerList.add(mockPlayer3);
        playerList.add(mockPlayer1);

        ArrayList  <RemotePlayerTest> sortedPlayerList= new ArrayList(0);
        sortedPlayerList.add(mockPlayer3);
        sortedPlayerList.add(mockPlayer1);
        sortedPlayerList.add(mockPlayer2);





        for (int i = 0; i < playerList.size() - 1; i++)  //runs for every element -1
        {
            int pos = i;
            if (playerList.get(pos + 1).getDistance() < playerList.get(pos).getDistance())  //if next value is less than current value
            {
                while (playerList.get(pos + 1).getDistance() < playerList.get(pos).getDistance()) //searches for proper place for value
                {
                    Collections.swap(playerList,pos+1,pos);
                    if (pos == 0)
                        break;
                    else
                        pos -= 1;
                }
            }
        }


        //run through the messages and ensure the selected message does not equal
        for(int i= 0; i < playerList.size(); i++) {
            assert(sortedPlayerList.get(i).getDistance() == playerList.get(i).getDistance());
        }


    }






    @Test
    public void sortPlayers(){
        //insertSortPlayers(l);

        ArrayList  <RemotePlayerTest> playerList= new ArrayList<RemotePlayerTest>(0);
        RemotePlayerTest player1 = new RemotePlayerTest();
        RemotePlayerTest player2 = new RemotePlayerTest();


        player1.setDistance(5.0);
        player2.setDistance(3.0);

        playerList.add(player2);
        playerList.add(player1);

        int expectedPlace1 = 1;
        int expectedPlace2 = 2;
        int expectedLocalPlace= 3;

        int localDistance = 0;
        int locPlace = 0;

        boolean locPlayerRanked = true;

        //operates on a sorted list of players
        for(int i = 0; i < playerList.size(); i++){
            if(playerList.get(i).getDistance() > localDistance) {
                playerList.get(i).setPlace(i + 1);
            }
            else{
                if(locPlayerRanked) {
                    locPlayerRanked = false;
                    locPlace = i + 1;
                }
                playerList.get(i).setPlace(i+2);
            }

        }

        //first place == first place remoteplayer's place attribute is false
        assert (expectedPlace1 != playerList.get(1).getPlace());
        //assert (expectedPlace2 != playerList.get(1).getPlace());
        //assert (expectedLocalPlace != 0);


    }





}
