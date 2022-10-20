package com.example.soteria

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/*
Name: Event
Description: A Room-compatible data class for safety events.

Details:
    - id: Primary key of integer
    - latitude: Double containing latitude of the event's recorded location
    - longitude: Double containing longitude of the event's recorded location
    - time: String containing the event's recorded time in [local time/UTC]
    - recording_id: Integer foreign key referencing the event's associated recording in the Recording table
 */

@Entity(foreignKeys =
        [ForeignKey(
            entity = Recording::class,
            childColumns = ["recording_id"],
            parentColumns = ["id"])
        ]
)
data class Event (
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "latitude") val latitude: Double?,
    @ColumnInfo(name = "longitude") val longitude: Double?,
    @ColumnInfo(name = "time") val time: String?,
    @ColumnInfo(name = "recording_id") val recording_id: Int?
)