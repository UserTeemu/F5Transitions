package io.github.tivj.f5transitions.asm.tweaker;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.tivj.f5transitions.TransitionsMod;
import io.github.tivj.f5transitions.asm.modifications.*;
import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;

public class ClassTransformer implements IClassTransformer {
    private final Multimap<String, ITransformer> transformerMap = ArrayListMultimap.create();

    public ClassTransformer() {
        registerTransformer(new EntityRendererTransformer());
        registerTransformer(new LayerCustomHeadTransformer());
        registerTransformer(new LayerHeldItemTransformer());
        registerTransformer(new MinecraftTransformer());
        registerTransformer(new RendererLivingEntityTransformer());
        registerTransformer(new RenderGlobalTransformer());
        registerTransformer(new ItemRendererTransformer());

        registerTransformer(new GuiOverlayDebugTransformerForDebug());
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

        TransitionsMod.LOGGER.info("Found {} transformers for {}", transformers.size(), transformedName);

        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, ClassReader.EXPAND_FRAMES);

        transformers.forEach(transformer -> {
            TransitionsMod.LOGGER.info("Applying transformer {} on {}...", transformer.getClass().getName(), transformedName);
            transformer.transform(node, transformedName);
        });

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        try {
            node.accept(writer);
        } catch (Throwable t) {
            TransitionsMod.LOGGER.error("Exception when transforming " + transformedName + ": " + t.getClass().getSimpleName());
            t.printStackTrace();
        }

        return writer.toByteArray();
    }
}
