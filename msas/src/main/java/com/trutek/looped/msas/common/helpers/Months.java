package com.trutek.looped.msas.common.helpers;

public enum  Months {

    January(1),
    February(2),
    March(3),
    April(4),
    May(5),
    June(6),
    July(7),
    August(8),
    September(9),
    October(10),
    November(11),
    December(12);


    private final int month;

    Months(int month){
        this.month = month;
    }

    public int getMonth(){
        return  this.month;
    }

    public static Months fromInt(int i){
        switch (i){
            case 1:
                return January;
            case 2:
                return February;
            case 3:
                return March;
            case 4:
                return April;
            case 5:
                return May;
            case 6:
                return June;
            case 7:
                return July;
            case 8:
                return August;
            case 9:
                return September;
            case 10:
                return October;
            case 11:
                return November;
            case 12:
                return December;
            default:
                return January;
        }
    }
}
