package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.util.DatabaseConnection;

/**
 * com.lordjoe.resource_guide.LoadRemote
 * User: Steve
 * Date: 5/5/25
 */
public class LoadRemote {


    public static void main(String[] args) throws Exception {
        DatabaseConnection.setREMOTE();
        MainApp.main(args);
    }
}
