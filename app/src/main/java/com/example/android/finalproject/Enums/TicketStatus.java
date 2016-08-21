package com.example.android.finalproject.Enums;

/**
 * Created by אוריה on 10/08/2016.
 */
public enum TicketStatus
{
    WaitForApproval("ממתין לאישור מנהל"), TicketClosed("התקלה נסגרה"), Approval("התקלה אושרה");

    private String status;

    private TicketStatus(String status)
    {
        this.status=status;
    }

    public String toString()
    {
        return status;
    }

}
