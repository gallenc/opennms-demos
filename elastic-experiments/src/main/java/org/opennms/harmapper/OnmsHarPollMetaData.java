package org.opennms.harmapper;

import java.util.Date;
import java.util.UUID;

public class OnmsHarPollMetaData {

	Date startTime = new Date();
	String pollerlocation = null;
	Long latitude = null;
	Long longitude = null;
	String pollerId = null;
	String pollerDns = null;
	String pollerIp = null;
	String pollId = UUID.randomUUID().toString();

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getPollerlocation() {
		return pollerlocation;
	}

	public void setPollerlocation(String pollerlocation) {
		this.pollerlocation = pollerlocation;
	}

	public Long getLatitude() {
		return latitude;
	}

	public void setLatitude(Long latitude) {
		this.latitude = latitude;
	}

	public Long getLongitude() {
		return longitude;
	}

	public void setLongitude(Long longitude) {
		this.longitude = longitude;
	}

	public String getPollerId() {
		return pollerId;
	}

	public void setPollerId(String pollerId) {
		this.pollerId = pollerId;
	}

	public String getPollerDns() {
		return pollerDns;
	}

	public void setPollerDns(String pollerDns) {
		this.pollerDns = pollerDns;
	}

	public String getPollerIp() {
		return pollerIp;
	}

	public void setPollerIp(String pollerIp) {
		this.pollerIp = pollerIp;
	}

	public String getPollId() {
		return pollId;
	}

	public void setPollId(String pollId) {
		this.pollId = pollId;
	}

}
