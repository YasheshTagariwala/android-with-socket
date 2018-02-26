package com.example.yash.nodesocketmessaging;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by Yash on 19-02-2018.
 */
public class JobSchedulerUtils {

    public static int JOB_ID = 101;

    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, SocketService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPeriodic(5 * 60 * 1000);//Runs Every Five(5) Minutes
//        builder.setPeriodic(30000);//Runs Every Thirty(30) Seconds
        builder.setPersisted(true);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public static void cancleJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOB_ID);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void scheduleJobForN(Context context) {
        ComponentName serviceComponent = new ComponentName(context, SocketService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPeriodic(5 * 60 * 1000, 5 * 60 * 1000);
        builder.setPersisted(true);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
}
