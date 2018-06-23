package akkamongo;

import java.util.Map;

public class Messages {

    public static class StartSystem {
    }

    public static class StopSystem {
    }

    public static class StartLoop {
    }

    public static class StopLoop {
    }

    public static class PrintResult {
    }

    public static class AllResult {
        String json;

        public AllResult(String json) {
            this.json = json;
        }

        public String getJson() {
            return json;
        }
    }

    public static class OneResult {
        Map<String, Object> result;

        public OneResult(Map<String, Object> result) {
            this.result = result;
        }

        public Map<String, Object> getResult() {
            return result;
        }
    }
}
