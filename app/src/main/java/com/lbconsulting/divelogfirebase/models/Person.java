package com.lbconsulting.divelogfirebase.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * This class holds a person's info.
 */
public class Person {

    private static final String NODE_DIVE_LOGS_AS_BUDDY = "diveLogsAsBuddy";
    private static final String NODE_DIVE_LOGS_AS_DIVE_MASTER = "diveLogsAsDiveMaster";
    private static final String NODE_DIVE_LOGS_AS_COMPANY = "diveLogsAsCompany";

    private static final String DEFAULT_BUDDY = "[None]";
    private static final String DEFAULT_DIVE_MASTER = "[None]";
    private static final String DEFAULT_COMPANY = "[None]";

    private static final DatabaseReference dbReference = FirebaseDatabase
            .getInstance().getReference();
    private static final String NODE_PEOPLE = "people";

    private boolean buddy;
    private boolean company;
    private String contactID;
    private boolean diveMaster;
    private String name;
    private String personUid;
    private String photoUrl;

    private Map<String, Boolean> diveLogsAsBuddy = new HashMap<>();
    private Map<String, Boolean> diveLogsAsDiveMaster = new HashMap<>();
    private Map<String, Boolean> diveLogsAsCompany = new HashMap<>();

    public Person() {

    }

    public Person(@NonNull String personName, @NonNull String personUid,
                  boolean isBuddy, boolean isDiveMaster, boolean isCompany,
                  @Nullable String diveLogUid) {
        this.name = personName;
        this.personUid = personUid;
        this.buddy = isBuddy;
        this.diveMaster = isDiveMaster;
        this.company = isCompany;
        this.photoUrl = MySettings.NOT_AVAILABLE;
        this.contactID = MySettings.NOT_AVAILABLE;
        diveLogsAsBuddy = new HashMap<>();
        diveLogsAsDiveMaster = new HashMap<>();
        diveLogsAsCompany = new HashMap<>();

        if (diveLogUid != null && !diveLogUid.equals(MySettings.NOT_AVAILABLE)) {
            if (isBuddy) {
                diveLogsAsBuddy.put(diveLogUid, true);
            }
            if (isDiveMaster) {
                diveLogsAsDiveMaster.put(diveLogUid, true);
            }
            if (isCompany) {
                diveLogsAsCompany.put(diveLogUid, true);
            }
        }
    }

    //region Getters and Setters
    public String getPersonUid() {
        return personUid;
    }

    public void setPersonUid(String personUid) {
        this.personUid = personUid;
    }

    public boolean isBuddy() {
        return buddy;
    }

    public void setBuddy(boolean buddy) {
        this.buddy = buddy;
    }

    public boolean isCompany() {
        return company;
    }

    public void setCompany(boolean company) {
        this.company = company;
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public boolean isDiveMaster() {
        return diveMaster;
    }

    public void setDiveMaster(boolean diveMaster) {
        this.diveMaster = diveMaster;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Map<String, Boolean> getDiveLogsAsBuddy() {
        return diveLogsAsBuddy;
    }

    public void setDiveLogsAsBuddy(Map<String, Boolean> diveLogsAsBuddy) {
        this.diveLogsAsBuddy = diveLogsAsBuddy;
    }

    public Map<String, Boolean> getDiveLogsAsMaster() {
        return diveLogsAsDiveMaster;
    }

    public void setDiveLogsAsDiveMaster(Map<String, Boolean> diveLogsAsDiveMaster) {
        this.diveLogsAsDiveMaster = diveLogsAsDiveMaster;
    }

    public Map<String, Boolean> getDiveLogsAsCompany() {
        return diveLogsAsCompany;
    }

    public void setDiveLogsAsCompany(Map<String, Boolean> diveLogsAsCompany) {
        this.diveLogsAsCompany = diveLogsAsCompany;
    }

    @Exclude
    public static Person getDefaultPerson(int btnId) {
        // TODO: Implement getDefaultPerson
        Person person = null;
        switch (btnId) {
            case R.id.btnDiveBuddy:
                person = new Person(DEFAULT_BUDDY, MySettings.NOT_AVAILABLE,
                        true, false, false, null);
                break;

            case R.id.btnDiveMaster:
                person = new Person(DEFAULT_DIVE_MASTER, MySettings.NOT_AVAILABLE,
                        false, true, false, null);
                break;

            case R.id.btnCompany:
                person = new Person(DEFAULT_COMPANY, MySettings.NOT_AVAILABLE,
                        false, false, true, null);
                break;
        }
        return person;
    }

    @Override
    public String toString() {
        return name;
    }
    //endregion Getters and Setters

    public static DatabaseReference nodeUserPersons(@NonNull String userUid) {
        return dbReference.child(NODE_PEOPLE).child(userUid);
    }

    public static DatabaseReference nodeUserPerson(@NonNull String userUid, @NonNull String
            personUid) {
        return nodeUserPersons(userUid).child(personUid);
    }

    private static DatabaseReference nodeUserPeopleAsBuddy(@NonNull String userUid, @NonNull
            String personUid) {
        return dbReference.child(NODE_PEOPLE).child(userUid).child(personUid).child
                (NODE_DIVE_LOGS_AS_BUDDY);
    }

    private static DatabaseReference nodeUserPeopleAsDiveMaster(@NonNull String userUid, @NonNull
            String personUid) {
        return dbReference.child(NODE_PEOPLE).child(userUid).child(personUid).child
                (NODE_DIVE_LOGS_AS_DIVE_MASTER);
    }

    private static DatabaseReference nodeUserPeopleAsCompany(@NonNull String userUid, @NonNull
            String personUid) {
        return dbReference.child(NODE_PEOPLE).child(userUid).child(personUid).child
                (NODE_DIVE_LOGS_AS_COMPANY);
    }

    public static void selectPersonForDiveLog(final String userUid, final String activeDiveLogUid,
                                              final int buttonID, final Person selectedPerson) {

        // To select a Person:
        //  1. Remove the active diveLog from the previous Person's record
        //  2. Add the the active diveLog to the isChecked Person's record
        //  3. Update the active diveLog with the isChecked Person's name and Uid, then save the
        // active diveLog

        // TODO: Implement selectPersonForDiveLog

        DiveLog.nodeUserDiveLog(userUid, activeDiveLogUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    final DiveLog activeDiveLog = dataSnapshot.getValue(DiveLog.class);
                    if (activeDiveLog != null) {
                        switch (buttonID) {
                            case R.id.btnDiveBuddy:
                                removeBuddy(userUid, activeDiveLog.getDiveBuddyPersonUid(),
                                        activeDiveLogUid);
                                addBuddy(userUid, selectedPerson.getPersonUid(), activeDiveLogUid);
//                            activeDiveLog.setDiveBuddy(selectedPerson.getName());
                                activeDiveLog.setDiveBuddyPersonUid(selectedPerson.getPersonUid());
                                DiveLog.save(userUid, activeDiveLog);
                                break;

                            case R.id.btnDiveMaster:
                                removeDiveMaster(userUid, activeDiveLog.getDiveMasterPersonUid(),
                                        activeDiveLogUid);
                                addDiveMaster(userUid, selectedPerson.getPersonUid(), activeDiveLogUid);
//                            activeDiveLog.setDiveMaster(selectedPerson.getName());
                                activeDiveLog.setDiveMasterPersonUid(selectedPerson.getPersonUid());
                                DiveLog.save(userUid, activeDiveLog);
                                break;

                            case R.id.btnCompany:
                                removeCompany(userUid, activeDiveLog.getDiveCompanyPersonUid(),
                                        activeDiveLogUid);
                                addCompany(userUid, selectedPerson.getPersonUid(), activeDiveLogUid);
//                            activeDiveLog.setDiveCompany(selectedPerson.getName());
                                activeDiveLog.setDiveCompanyPersonUid(selectedPerson.getPersonUid());
                                DiveLog.save(userUid, activeDiveLog);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Exclude
    public static void addBuddy(@NonNull String userUid,
                                @NonNull String personUid,
                                @NonNull String diveLogUid) {
        if (!personUid.equals(MySettings.NOT_AVAILABLE)) {
            nodeUserPeopleAsBuddy(userUid, personUid).child(diveLogUid).setValue(true);
        }
    }

    @Exclude
    public static void removeBuddy(@NonNull String userUid,
                                   @NonNull String personUid,
                                   @NonNull String diveLogUid) {
        if (!personUid.equals(MySettings.NOT_AVAILABLE)) {
            nodeUserPeopleAsBuddy(userUid, personUid).child(diveLogUid).removeValue();
        }
    }

    public static void changeDiveBuddy(@NonNull String userUid,
                                       @NonNull String oldPersonUid,
                                       @NonNull String newPersonUid,
                                       @NonNull String diveLogUid) {
        removeBuddy(userUid, oldPersonUid, diveLogUid);
        addBuddy(userUid, newPersonUid, diveLogUid);
    }


    @Exclude
    public static void addDiveMaster(@NonNull String userUid,
                                     @NonNull String personUid,
                                     @NonNull String diveLogUid) {
        if (!personUid.equals(MySettings.NOT_AVAILABLE)) {
            nodeUserPeopleAsDiveMaster(userUid, personUid).child(diveLogUid).setValue(true);
        }
    }

    @Exclude
    public static void removeDiveMaster(@NonNull String userUid,
                                        @NonNull String personUid,
                                        @NonNull String diveLogUid) {
        if (!personUid.equals(MySettings.NOT_AVAILABLE)) {
            nodeUserPeopleAsDiveMaster(userUid, personUid).child(diveLogUid).removeValue();
        }
    }

    public static void changeDiveMaster(@NonNull String userUid,
                                        @NonNull String oldPersonUid,
                                        @NonNull String newPersonUid,
                                        @NonNull String diveLogUid) {
        removeDiveMaster(userUid, oldPersonUid, diveLogUid);
        addDiveMaster(userUid, newPersonUid, diveLogUid);
    }

    @Exclude
    public static void addCompany(@NonNull String userUid,
                                  @NonNull String personUid,
                                  @NonNull String diveLogUid) {
        if (!personUid.equals(MySettings.NOT_AVAILABLE)) {
            nodeUserPeopleAsCompany(userUid, personUid).child(diveLogUid).setValue(true);
        }
    }

    @Exclude
    public static void removeCompany(@NonNull String userUid,
                                     @NonNull String personUid,
                                     @NonNull String diveLogUid) {
        if (!personUid.equals(MySettings.NOT_AVAILABLE)) {
            nodeUserPeopleAsCompany(userUid, personUid).child(diveLogUid).removeValue();
        }
    }

    public static void changeDiveCompany(@NonNull String userUid,
                                         @NonNull String oldPersonUid,
                                         @NonNull String newPersonUid,
                                         @NonNull String diveLogUid) {
        removeCompany(userUid, oldPersonUid, diveLogUid);
        addCompany(userUid, newPersonUid, diveLogUid);
    }

    public static String save(@NonNull String userUid, Person person) {

        if (person.getPersonUid() == null || person.getPersonUid().isEmpty()
                || person.getPersonUid().equals(MySettings.NOT_AVAILABLE)) {
            person.setPersonUid(nodeUserPersons(userUid).push().getKey());
            Timber.i("Created person \"%s\".", person.getName());
        }
        nodeUserPersons(userUid).child(person.getPersonUid()).setValue(person);
        Timber.i("Saved person \"%s\".", person.getName());

        return person.getPersonUid();
    }


    public static final Comparator<Person> sortOrderAscending = new Comparator<Person>() {
        public int compare(Person selectionValue1, Person selectionValue2) {
            return selectionValue1.getName().compareToIgnoreCase(selectionValue2.getName());
        }
    };

    public static boolean okToSavePerson(@NonNull List<Person> people,
                                         @NonNull Person proposedPerson) {
        boolean result = true;
        if (people.size() > 0) {
            for (Person person : people) {
                int comparison = person.getName().compareToIgnoreCase(proposedPerson.getName());
                if (comparison == 0) {
                    // we've found a Person with the same name as the proposeName
                    // check if the Uids are the same
                    // the user has changed the case of some of the letters in the Person's name
                    result = person.getPersonUid().equals(proposedPerson.getPersonUid());
                    break;
                }
            }
        }

        return result;
    }

    public static void remove(String userUid, Person person) {
        nodeUserPersons(userUid).child(person.getPersonUid()).removeValue();
    }

    public static Person findPersonByName(@NonNull String soughtPersonName,
                                          @NonNull List<Person> people) {
        Person foundPerson = null;
        for (Person person : people) {
            if (person.getName().equalsIgnoreCase(soughtPersonName)) {
                foundPerson = person;
                break;
            }
        }
        return foundPerson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return personUid.equals(person.personUid);
    }

    @Override
    public int hashCode() {
        return personUid.hashCode();
    }
}

