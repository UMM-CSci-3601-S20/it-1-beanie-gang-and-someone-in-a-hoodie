package umm3601;

import java.util.Collections;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;

import io.javalin.http.HttpResponseException;

public class UnprocessableResponse extends HttpResponseException {

  public UnprocessableResponse() {
    super(HttpStatus.UNPROCESSABLE_ENTITY_422, "Unprocessable", Collections.<String, String>emptyMap());
  }

  public UnprocessableResponse(String msg) {
    super(HttpStatus.UNPROCESSABLE_ENTITY_422, msg, Collections.<String, String>emptyMap());
  }

  public UnprocessableResponse(Map<String, String> dt) {
    super(HttpStatus.UNPROCESSABLE_ENTITY_422, "Unprocessable", dt);
  }

  public UnprocessableResponse(String msg, Map<String, String> dt) {
    super(HttpStatus.UNPROCESSABLE_ENTITY_422, msg, dt);
  }

}
