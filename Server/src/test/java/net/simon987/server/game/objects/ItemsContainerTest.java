package net.simon987.server.game.objects;

import net.simon987.server.game.item.ItemCopper;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;

public class ItemsContainerTest {

    @Test
    public void shouldBeSerializableToMongoFormat() {
        ItemsContainer itemsContainer = new ItemsContainer(2);
        ItemCopper item = new ItemCopper();
        itemsContainer.placeItem(item);

        Document document = itemsContainer.mongoSerialise();

        ItemsContainer deserialized = new ItemsContainer(document);
        Assert.assertTrue(deserialized.canTakeItem(item.getId()));
    }

    @Test
    public void shouldPlaceItemWhenCapacityAllowsIt() {
        ItemsContainer itemsContainer = new ItemsContainer(1);

        boolean result = itemsContainer.placeItem(new ItemCopper());

        Assert.assertEquals(true, result);
    }

    @Test
    public void shouldNotPlaceItemWhenThereIsNoCapacityLeft() {
        ItemsContainer itemsContainer = new ItemsContainer(1);

        itemsContainer.placeItem(new ItemCopper());
        boolean result = itemsContainer.placeItem(new ItemCopper());

        Assert.assertEquals(false, result);
    }

    @Test
    public void shouldNotBeAbleToTakeItemWhenItWasNotPlacedBefore() {
        ItemsContainer itemsContainer = new ItemsContainer(1);

        boolean result = itemsContainer.canTakeItem(1);

        Assert.assertEquals(false, result);
    }

    @Test
    public void shouldBeAbleToTakeItemIfItWasPlacedBefore() {
        ItemsContainer itemsContainer = new ItemsContainer(1);
        ItemCopper item = new ItemCopper();

        itemsContainer.placeItem(item);
        boolean result = itemsContainer.canTakeItem(item.getId());

        Assert.assertEquals(true, result);
    }

    @Test
    public void shouldNotBeAbleToTakeItemAfterItWasTaken() {
        ItemsContainer itemsContainer = new ItemsContainer(1);
        ItemCopper item = new ItemCopper();

        itemsContainer.placeItem(item);
        itemsContainer.takeItem(item.getId());
        boolean result = itemsContainer.canTakeItem(item.getId());

        Assert.assertEquals(false, result);
    }
}
