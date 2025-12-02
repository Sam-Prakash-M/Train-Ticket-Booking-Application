package com.samprakash.ticketbookmodel;

import org.bson.Document;

public record SeatCounts(int freeCNF, int freeRAC, int freeWL, Document availabilityDoc) {}

