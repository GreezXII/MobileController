package com.greezxii.mobilecontroller.repository;

import com.greezxii.mobilecontroller.database.Inspection;

import java.util.ArrayList;
import java.util.List;

public abstract class QueryResult {
    private QueryResult() {}

    public static final class Success extends QueryResult {
        public List<Inspection> data;
        public Success() {
            this.data = null;
        }
        public Success(List<Inspection> data) {
            this.data = data;
        }
    }

    public static final class Error extends QueryResult {
        public Exception exception;
        public Error(Exception exception) {
            this.exception = exception;
        }
    }
}
