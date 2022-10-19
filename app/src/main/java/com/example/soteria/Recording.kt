package com.example.soteria

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Todo: Is boolean okay for recording format?
// Todo: Is seconds okay for recording length unit?

/*
Name: Event
Description: A Room-compatible data class for event recordings.

Details:
    - id: Primary key of integer
    - gcp_location: String containing link to the recording's Google Cloud Platform location
    - length: Integer containing length of the recording in seconds
    - format: Boolean encoding whether the recording is audio or video
        - format = 0: audio recording
        - format = 1: video recording
 */

@Entity
data class Recording (
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "gcp_location") val gcp_location: String?,
    @ColumnInfo(name = "length") val length: Int?,
    @ColumnInfo(name = "format") val format: Boolean?

)