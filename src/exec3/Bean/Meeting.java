package exec3.Bean;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Meeting implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    private int meetingID;
    private String title;
    private Date begin;
    private Date end;
    private User initiator;
    private User participant;

    public int getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(int meetingID) {
        this.meetingID = meetingID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public User getInitiator() {
        return initiator;
    }

    public void setInitiator(User initiator) {
        this.initiator = initiator;
    }

    public User getParticipant() {
        return participant;
    }

    public void setParticipant(User participant) {
        this.participant = participant;
    }

    public Meeting(int meetingID, String title, Date begin, Date end, User initiator, User participant) {
        this.meetingID = meetingID;
        this.title = title;
        this.begin = begin;
        this.end = end;
        this.initiator = initiator;
        this.participant = participant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeting meeting = (Meeting) o;
        return meetingID == meeting.meetingID && Objects.equals(title, meeting.title) && Objects.equals(begin, meeting.begin) && Objects.equals(end, meeting.end) && Objects.equals(initiator, meeting.initiator) && Objects.equals(participant, meeting.participant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meetingID, title, begin, end, initiator, participant);
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "meetingID=" + meetingID +
                ", title='" + title + '\'' +
                ", begin=" + begin +
                ", end=" + end +
                ", initiator=" + initiator +
                ", participant=" + participant +
                '}';
    }
}
