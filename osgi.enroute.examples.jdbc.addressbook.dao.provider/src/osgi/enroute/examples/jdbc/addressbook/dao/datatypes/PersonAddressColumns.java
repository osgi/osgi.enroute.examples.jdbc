package osgi.enroute.examples.jdbc.addressbook.dao.datatypes;

public enum PersonAddressColumns {

    PERSON_ID{

        @Override
        public String columnName() {
            return "person_id";
        }

    },
    EMAIL_ADDRESS{

        @Override
        public String columnName() {
           
            return "email_address";
        }

    },
    CITY{

        @Override
        public String columnName() {
            return "city";
        }

    },
    COUNTRY{

        @Override
        public String columnName() {
            return "country";
        }

    };

   public abstract String columnName();
}
