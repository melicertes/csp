package com.intrasoft.csp.regrep.routes;

public interface ContextUrl {

    enum Api {
        COUNT("_count");

        private final String value;

        Api(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }


}
