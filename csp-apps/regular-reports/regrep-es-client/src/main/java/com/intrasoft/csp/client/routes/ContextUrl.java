package com.intrasoft.csp.client.routes;

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
