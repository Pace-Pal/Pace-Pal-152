package com.group2.pacepal;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;



import static org.mockito.Mockito.mock;


@RunWith(MockitoJUnitRunner.Silent.class)
public class InstantMessagingTest {

    @Mock
    TextMessageTest textMessage;

    /*
        Purpose: A test that emulates the search used in chatChannelFragment.kt 's updateRecyclerView function. It will assert that at the end of the
                 search, there are no messages in the spylist that match the message selected from the DynamicSpyList. A match is when a mockMessage's
                 time and messageText values are equal.

    */

    @Test
    public void isElem() {

        TextMessageTest mockMessage1 = Mockito.spy( TextMessageTest.class);
        TextMessageTest mockMessage2 = mock(TextMessageTest.class);
        TextMessageTest mockMessage3 = mock(TextMessageTest.class);
        TextMessageTest mockMessage4 = mock(TextMessageTest.class);
        TextMessageTest mockMessage5 = mock(TextMessageTest.class);

        //List spyList = Mockito.spy(new ArrayList());
        ArrayList<TextMessageTest> spyList = new ArrayList<TextMessageTest>(0);
        spyList.add(mockMessage1);
        spyList.add(mockMessage2);
        spyList.add(mockMessage3);
        spyList.add(mockMessage4);

        //A set of example text messages that can be in the chatchannel
        Mockito.when(mockMessage1.getTimes()).thenReturn("1:00 PM");
        Mockito.when(mockMessage1.getMessageText()).thenReturn("Message Text");

        Mockito.when(mockMessage2.getTimes()).thenReturn("1:05 PM");
        Mockito.when(mockMessage2.getMessageText()).thenReturn("Message Text");

        Mockito.when(mockMessage3.getTimes()).thenReturn("1:10 PM");
        Mockito.when(mockMessage3.getMessageText()).thenReturn("Garbanzos");

        Mockito.when(mockMessage4.getTimes()).thenReturn("1:15 PM");
        Mockito.when(mockMessage4.getMessageText()).thenReturn("Clamato");

        Mockito.when(mockMessage5.getTimes()).thenReturn("1:15 PM");
        Mockito.when(mockMessage5.getMessageText()).thenReturn("Corn");



        ArrayList<TextMessageTest> DynamicSpyList = new ArrayList<>();
        DynamicSpyList.add(mockMessage1);
        DynamicSpyList.add(mockMessage2);
        DynamicSpyList.add(mockMessage3);
        DynamicSpyList.add(mockMessage4);
        DynamicSpyList.add(mockMessage5);

        //the app logic

        boolean isElem = false;
        int tempIndex = 1;
        for (int i = DynamicSpyList.size(); i > 0; i--) {
            isElem = false;
            for (int j= spyList.size(); j > 0; j--) {
                if (spyList.get(j-1).getMessageText() == DynamicSpyList.get(i-1).getMessageText() &&  spyList.get(j-1).getTimes() == DynamicSpyList.get(i-1).getTimes() ) {
                    isElem = true;
                }
            }

            if (isElem == false) {
                tempIndex = i-1;
                break;
            }
        }


        //run through the messages and ensure the selected message does not equal
        for(int i= 0; i < spyList.size(); i++) {
            assert(DynamicSpyList.get(tempIndex) != spyList.get(i));
        }


        return;
    }





}
