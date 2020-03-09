package umm3601.note;

import org.mongojack.Id;
import org.mongojack.ObjectId;

public class Note {

  @ObjectId @Id
  public String _id;

  public String ownerID;
  public String body;
  public String addDate;
  public String expireDate;
  public String status;

}
