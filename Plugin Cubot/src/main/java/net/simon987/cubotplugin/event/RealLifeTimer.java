package net.simon987.cubotplugin.event;

import java.util.Timer;
import java.util.TimerTask;

public class RealLifeTimer{

    private int time;

    private Timer timer;

    public RealLifeTimer(){
        timer = new Timer();
        time = 0;
    }

    public RealLifeTimer(int startTime){
        timer = new Timer();
        time = startTime;
    }

    public void run(){

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run(){
                time++;
            }
        },0,1);
    }

    public void stop(){
        timer.cancel();
        timer.purge();
    }

    public int getTime(){
        return this.time;
    }
}