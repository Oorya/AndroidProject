package com.example.android.finalproject.Utility;


import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.android.finalproject.Ticket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CalendarUtil extends Activity implements EasyPermissions.PermissionCallbacks {
    private static final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    private static final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    private static final String TAG = "CalendarUtil";

    private static com.google.api.services.calendar.Calendar mService;
    private static Context _context;

    private GoogleAccountCredential mCredential;
    private ProgressDialog mProgress;
    //private CalendarAdpter mAdpter;
    private List<CalendarListEntry> items1;



    public CalendarUtil(Context context
                        //CalendarAdpter adapter
                         ) {
        //this.mAdpter = adapter;
        this._context = context;
        mProgress = new ProgressDialog(context);
        mProgress.setMessage("Calling Google Calendar API ...");

        // Initialize credentials and service object.
        this.mCredential = GoogleAccountCredential.usingOAuth2(
                context, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        mCredential.setSelectedAccountName("shealtiel84@gmail.com");
        // Initialize calendar service object.
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, this.mCredential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    public void getResultsFromApi() {
        //if (!isGooglePlayServicesAvailable()) {
        //  acquireGooglePlayServices();
        // } else
        if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(_context, "isDeviceOnline false", Toast.LENGTH_LONG).show();
        } else {
            new MakeRequestTask().execute();
        }
    }


    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                _context, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = ((Activity) _context).getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                ((Activity) _context).startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    _context,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(_context, "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_LONG).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(_context);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(_context);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                (Activity) this._context,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }


    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, List<Ticket>, List<Ticket>> {

        private Exception mLastError = null;

        public MakeRequestTask() {
        }

        /**
         * Background task to call Google Calendar API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<Ticket> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         *
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<Ticket> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<Ticket> eventStrings = new ArrayList<Ticket>();
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            for (Event event : items) {


                eventStrings.add(new Ticket());
            }
            return eventStrings;
        }


        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        /*@Override
        protected void onPostExecute(List<EventUtil> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                Toast.makeText(_context, "no resolt", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(_context, "resolt...", Toast.LENGTH_LONG).show();
                mAdpter.setList(output);
                mAdpter.notifyDataSetChanged();

            }
        }

        /*
        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                        showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }



    }

    public void addNewEvent(final String[] eventDitile) {


        new AsyncTask<Void, Void, Void>() {

            Event event = new Event()
                    .setSummary(eventDitile[0])
                    .setLocation(eventDitile[1])
                    .setDescription(eventDitile[2]);

            @Override
            protected Void doInBackground(Void... params) {
                SimpleDateFormat fromUser = new SimpleDateFormat("dd/MM/yyyy");

                try {

                    Date startDate = new Date(); // Or a date from the database
                    Date endDate = fromUser.parse(eventDitile[3]); // An all-day event is 1 day (or 86400000 ms) long

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String startDateStr = dateFormat.format(startDate);
                    String endDateStr = dateFormat.format(endDate);

                    // Out of the 6 methods for creating a DateTime object with no time element, only the String version works
                    DateTime startDateTime = new DateTime(startDateStr);
                    DateTime endDateTime = new DateTime(endDateStr);

                    // Must use the setDate() method for an all-day event (setDateTime() is used for timed events)
                    EventDateTime startEventDateTime = new EventDateTime().setDate(startDateTime);
                    EventDateTime endEventDateTime = new EventDateTime().setDate(endDateTime);

                    event.setStart(startEventDateTime);
                    event.setEnd(endEventDateTime);

                } catch (ParseException e) {

                }
                String calendarId = eventDitile[4];
                try {
                    event = mService.events().insert(calendarId, event).execute();
                } catch (IOException e) {

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(_context, "event add...", Toast.LENGTH_LONG).show();
                EventUtil eventUtil = new EventUtil(event.getStart().toString(), event.getSummary(), event.getId());
                mAdpter.addToList(eventUtil);
                mAdpter.notifyDataSetChanged();
            }
        }.execute();
    }

    public void delete(String Id) {
        new EventAction(Id, 1, mAdpter).execute();
    }

    public void update(String Id, String updateString) {
        Log.d(TAG, "update:in update ");
        new EventAction(Id, 2, updateString, mAdpter).execute();
    }

    public EventUtil getEvent(String Id) {
        new EventAction(Id, 3, mAdpter).execute();
        return null;
    }

    private class EventAction extends AsyncTask<Void, Event, Event> {
        int task;
        String updateString, Id;
        Event event;

        public EventAction(String id, int task, CalendarAdpter AD) {
            this.Id = id;
            this.task = task;
        }

        public EventAction(String id, int task, String updateString, CalendarAdpter AD) {
            this.Id = id;
            this.task = task;
            this.updateString = updateString;
        }

        @Override
        protected Event doInBackground(Void... params) {
            try {
                switch (this.task) {
                    case 1:
                        delete(this.Id);
                        return event = null;
                    case 2:
                        return update(this.Id, this.updateString);
                    case 3:
                        return getEvent(this.Id);
                    default:
                        return event = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private void delete(String id) throws IOException {
            mService.events().delete("primary", id).execute();
        }

        private Event update(String id, String updateString) throws IOException {
            // Retrieve the event from the API
            Event event;
            Event updatedEvent;
            event = mService.events().get("primary", id).execute();
            // Make a change
            event.setSummary(updateString);
            // Update the event
            updatedEvent = mService.events().update("primary", event.getId(), event).execute();

            return updatedEvent;
        }

        private Event getEvent(String id) throws IOException {
            event = mService.events().get("primary", id).execute();
            return event;
        }


        @Override
        protected void onPostExecute(Event event) {
            super.onPostExecute(event);
            Log.d(TAG, "update:in onPostExecute ");

            EventUtil eventUtil = null;
            if (this.task == 3) {
                eventUtil = new EventUtil(event.getStart().toString(), event.getSummary(), event.getId());
            }
            if (this.task == 2) {
                eventUtil = new EventUtil(event.getStart().toString(), event.getSummary(), event.getId());
            }
            if (eventUtil != null) {
                mAdpter.addToList(eventUtil);
            }
            mAdpter.notifyDataSetChanged();
        }

    }

    public void calendarView() {
        Date NOW = new Date();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, NOW.getTime());
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(builder.build());
        _context.startActivity(intent);
        Log.d(TAG, NOW.toString());

    }

    public String createdCalendar()
    {
        
        final Uri CAL_URI = CalendarContract.Calendars.CONTENT_URI;

        final ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Calendars.ACCOUNT_NAME, "shealtiel84@gmail.com");
        cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        cv.put(CalendarContract.Calendars.NAME, "shaltyTest");
        cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "shaltyTest");
        cv.put(CalendarContract.Calendars.CALENDAR_COLOR, 0xEA8561);
        //user can only read the calendar
        cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, "shealtiel84@gmail.com");
        cv.put(CalendarContract.Calendars.VISIBLE, 1);
        cv.put(CalendarContract.Calendars.SYNC_EVENTS, 1);


        ContentResolver cr = _context.getContentResolver();


        //insert the calendar into the database
        if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
           Toast.makeText(_context,"NO permission ",Toast.LENGTH_LONG).show();
        }
        Uri newUri = cr.insert(CAL_URI, cv);

        final Long calId = Long.parseLong(newUri.getLastPathSegment());
        

        return null;
    }
    public void sherCal(String scopeValue)
    {
       final AclRule rule = new AclRule();
       final AclRule.Scope scope = new AclRule.Scope();
        scope.setType("user").setValue(scopeValue);
        rule.setScope(scope).setRole("writer");

        new AsyncTask<Void,Void,Void>()
        {

            @Override
            protected Void doInBackground(Void... params) 
            {
                try {
                    AclRule createdRule =mService.acl().insert("primary", rule).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(_context,"ACL INSERT  success...",Toast.LENGTH_LONG).show();
            }
        }.execute();
     
    }
    public List<CalendarListEntry> getCalList()
    {
        new AsyncTask<Void,Void,Void>()
        {
            List<CalendarListEntry> items;
            CalendarListEntry calendarListEntry;
            String pageToken = null;
           
            @Override
            protected Void doInBackground(Void... params)
            {
                do {
                    CalendarList calendarList = null;
                    try {
                        calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    items= calendarList.getItems();
                    pageToken = calendarList.getNextPageToken();
                } while (pageToken != null);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                for (CalendarListEntry calendarListEntry : items)
                Toast.makeText(_context,calendarListEntry.getDescription(),Toast.LENGTH_LONG).show();
                items1=items;
            }
        }.execute();
        return items1;*/
    }
}
