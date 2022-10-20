package com.example.soteria.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

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
    - event_id:  Integer foreign key referencing the recording's associated event in the Event table
 */

@Entity(foreignKeys =
            [ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["event_id"],
            onDelete = CASCADE)
            ]
)
data class Recording (
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "gcp_location") val gcp_location: String?,
    @ColumnInfo(name = "length") val length: Int?,
    @ColumnInfo(name = "format") val format: Boolean?,
    @ColumnInfo(name = "event_id") val event_id: Int?
)