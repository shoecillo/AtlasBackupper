package com.sh.atlas.app.dto;

public class ConfigDTO {

	
	private String atlasPath;
	
	private String backupPath;
	
	private Integer maxBackups;
	
	private String cronBackup;
	
	private String cronClean;

	public String getAtlasPath() {
		return atlasPath;
	}

	public void setAtlasPath(String atlasPath) {
		this.atlasPath = atlasPath;
	}

	public String getBackupPath() {
		return backupPath;
	}

	public void setBackupPath(String backupPath) {
		this.backupPath = backupPath;
	}

	public Integer getMaxBackups() {
		return maxBackups;
	}

	public void setMaxBackups(Integer maxBackups) {
		this.maxBackups = maxBackups;
	}

	public String getCronBackup() {
		return cronBackup;
	}

	public void setCronBackup(String cronBackup) {
		this.cronBackup = cronBackup;
	}

	public String getCronClean() {
		return cronClean;
	}

	public void setCronClean(String cronClean) {
		this.cronClean = cronClean;
	}
	
	
	
}
