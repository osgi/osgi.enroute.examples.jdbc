package osgi.enroute.examples.jdbc.addressbook.dao.datatypes;

public enum PersonColumns {

    PERSON_ID{

        @Override
        public String columnName() {
            return "person_id";
        }

    },
    FIRST_NAME{

        @Override
        public String columnName() {
           
            return "first_name";
        }

    },
    LAST_NAME{

        @Override
        public String columnName() {
            return "last_name";
        }

    };

   public abstract String columnName();
}
