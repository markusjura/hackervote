package utils

import org.bson.types.ObjectId

object Conversions {  
  /**
   * String / ObjectId
   */
  implicit def objectIdToString(objectId: ObjectId): String = objectId.toString 
  implicit def stringToObjectId(string: String): ObjectId = new ObjectId(string)
}