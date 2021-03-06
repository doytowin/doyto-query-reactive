package win.doyto.query.reactive.mongodb.test.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import win.doyto.query.entity.MongoEntity;
import win.doyto.query.mongodb.entity.MongoPersistable;

/**
 * SizeEntity
 *
 * @author f0rb on 2021-11-23
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(database = "doyto", collection = "c_inventory")
public class InventorySize extends MongoPersistable<ObjectId> {
    private Double h;
    private Double w;
    private String uom;
}
