package noname.blockbuster;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.client.ActorsPack;
import noname.blockbuster.client.gui.GuiActorSkin;
import noname.blockbuster.client.gui.GuiCamera;
import noname.blockbuster.client.render.ActorRender;
import noname.blockbuster.client.render.CameraRender;
import noname.blockbuster.entity.ActorEntity;
import noname.blockbuster.entity.CameraEntity;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    public static ActorsPack actorPack;

    /**
     * Register mod items, blocks, tile entites and entities, and load
     * item, block models and register entity renderer.
     */
    @Override
    public void preLoad(FMLPreInitializationEvent event)
    {
        super.preLoad(event);

        this.registerItemModel(Blockbuster.cameraItem, Blockbuster.path("cameraItem"));
        this.registerItemModel(Blockbuster.cameraConfigItem, Blockbuster.path("cameraConfigItem"));
        this.registerItemModel(Blockbuster.recordItem, Blockbuster.path("recordItem"));
        this.registerItemModel(Blockbuster.registerItem, Blockbuster.path("registerItem"));
        this.registerItemModel(Blockbuster.skinManagerItem, Blockbuster.path("skinManagerItem"));

        this.registerItemModel(Blockbuster.directorBlock, Blockbuster.path("directorBlock"));

        this.registerEntityRender(CameraEntity.class, new CameraRender.CameraFactory());
        this.registerEntityRender(ActorEntity.class, new ActorRender.ActorFactory());

        String path = event.getSuggestedConfigurationFile().getAbsolutePath();

        this.injectResourcePack(path.substring(0, path.length() - 4));
    }

    /**
     * Inject actors resource pack
     */
    private void injectResourcePack(String configFolder)
    {
        try
        {
            Field field = Minecraft.class.getDeclaredField("defaultResourcePacks");
            field.setAccessible(true);

            List<IResourcePack> packs = (List<IResourcePack>) field.get(Minecraft.getMinecraft());

            packs.add(actorPack = new ActorsPack(configFolder + "/skins"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Register block model
     */
    protected void registerItemModel(Block block, String path)
    {
        this.registerItemModel(Item.getItemFromBlock(block), path);
    }

    /**
     * Register item model
     */
    protected void registerItemModel(Item item, String path)
    {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(path, "inventory"));
    }

    /**
     * Register entity renderer
     */
    protected void registerEntityRender(Class eclass, IRenderFactory factory)
    {
        RenderingRegistry.registerEntityRenderingHandler(eclass, factory);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        Entity entity = world.getEntityByID(x);

        if (ID == 0)
        {
            return new GuiCamera((CameraEntity) entity);
        }
        else if (ID == 1)
        {
            return new GuiActorSkin((ActorEntity) entity);
        }

        return null;
    }
}