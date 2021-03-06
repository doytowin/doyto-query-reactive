package win.doyto.query.reactive.mongodb.test.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import win.doyto.query.core.PageQuery;

/**
 * InventoryQuery
 *
 * @author f0rb on 2021-11-23
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InventoryQuery extends PageQuery {
    private String itemContain;
    private String status;
    private SizeQuery size;
}
