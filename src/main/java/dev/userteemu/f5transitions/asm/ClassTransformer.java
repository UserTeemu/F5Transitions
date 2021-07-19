package dev.userteemu.f5transitions.asm;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.userteemu.f5transitions.asm.modifications.*;
import dev.userteemu.f5transitions.asm.modifications.opacity.*;
import dev.userteemu.f5transitions.asm.modifications.compatibility.perspectivemod.PerspectiveModTransformer;
import dev.userteemu.f5transitions.asm.modifications.compatibility.perspectivemod.RotationTransititionTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;

public class ClassTransformer implements IClassTransformer {
    private final Multimap<String, ITransformer> transformerMap = ArrayListMultimap.create();
    public static final Logger LOGGER = LogManager.getLogger("F5 Transitions Transformers");

    public ClassTransformer() {
        registerTransformer(new EntityRendererTransformer());
        registerTransformer(new MinecraftTransformer());
        registerTransformer(new RenderGlobalTransformer());
        registerTransformer(new ItemRendererTransformer());

        // opacity transformers
        registerTransformer(new LayerHeldItemTransformer());
        registerTransformer(new RendererLivingEntityTransformer());
        registerTransformer(new LayerArmorBaseTransformer());
        registerTransformer(new LayerCustomHeadTransformer());
        registerTransformer(new LayerCapeTransformer());
        registerTransformer(new LayerDeadmau5HeadTransformer());
        registerTransformer(new LayerArrowTransformer());

        // block aabb provider transformers
        registerTransformer(new BlockAABBProviderTransformer.BlockTransformer());
        registerTransformer(new BlockAABBProviderTransformer.BlockStairsTransformer());
        registerTransformer(new BlockAABBProviderTransformer.PlaceholderMethodReplacerTransformer());

        // debug transformer
        registerTransformer(new GuiOverlayDebugTransformerForDebug());

        // DJTheRedstoner's PerspectiveModv4 compatibility
        registerTransformer(new PerspectiveModTransformer());
        registerTransformer(new RotationTransititionTransformer());
    }

    private void registerTransformer(ITransformer transformer) {
        for (String cls : transformer.getClassName()) {
            transformerMap.put(cls, transformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null) return null;

        Collection<ITransformer> transformers = transformerMap.get(transformedName);
        if (transformers.isEmpty()) return bytes;

        LOGGER.info("Found {} transformers for {}", transformers.size(), transformedName);

        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, ClassReader.EXPAND_FRAMES);

        for (ITransformer transformer : transformers) {
            LOGGER.info("Applying transformer {} on {}...", transformer.getClass().getName(), transformedName);
            transformer.transform(node, transformedName);
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        try {
            node.accept(writer);
        } catch (Throwable t) {
            LOGGER.error("Exception when transforming " + transformedName + ": " + t.getClass().getSimpleName());
            t.printStackTrace();
        }

        return writer.toByteArray();
    }
}
