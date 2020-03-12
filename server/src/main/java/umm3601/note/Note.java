package umm3601.note;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mongodb.lang.Nullable;

import org.mongojack.Id;
import org.mongojack.ObjectId;

public class Note {

  @ObjectId @Id
  public String _id;

  public String ownerID;
  public String body;
  public String addDate;

  @Nullable @JsonInclude(Include.NON_NULL)
  public String expireDate;

  public String status;

}
