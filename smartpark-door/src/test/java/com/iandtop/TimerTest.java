package com.iandtop;

import com.iandtop.front.smartpark.door.util.DoorConstants;
import io.vertx.core.Vertx;

/**
 * Created by Administrator on 2016/10/5.
 */
public class TimerTest {
    public static Long timerID = null;
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        int[] i = new int[1];
        timerID = vertx.setPeriodic(500,as->{
            System.out.println(as);
            i[0]++;
            if(i[0]>=10){
                vertx.cancelTimer(timerID);
                System.out.println(as+" stoped.");
            }
        });
    }
}


