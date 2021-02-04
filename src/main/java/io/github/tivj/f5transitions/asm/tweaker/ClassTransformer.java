package io.github.tivj.f5transitions.asm.tweaker;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.tivj.f5transitions.TransitionsMod;
import io.github.tivj.f5transitions.asm.modifications.*;
import io.github.tivj.f5transitions.asm.modifications.opacity.LayerArmorBaseTransformer;
import io.github.tivj.f5transitions.asm.modifications.opacity.LayerHeldItemAndCustomHeadTransformer;
import io.github.tivj.f5transitions.asm.tweaker.transformer.ITransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.CheckClassAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClassTransformer implements IClassTransformer {
    private final Multimap<String, ITransformer> transformerMap = ArrayListMultimap.create();
    private static final boolean debug = false;
    private static final Logger logger = TransitionsMod.LOGGER;

    public ClassTransformer() {
        registerTransformer(new EntityRendererTransformer());
        registerTransformer(new LayerHeldItemAndCustomHeadTransformer());
        registerTransformer(new MinecraftTransformer());
        registerTransformer(new RendererLivingEntityTransformer());
        registerTransformer(new RenderGlobalTransformer());
        registerTransformer(new ItemRendererTransformer());
        registerTransformer(new LayerArmorBaseTransformer());

        registerTransformer(new BlockAABBProviderTransformer.BlockTransformer());
        registerTransformer(new BlockAABBProviderTransformer.BlockStairsTransformer());
        registerTransformer(new BlockAABBProviderTransformer.PlaceholderMethodReplacerTransformer());

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

        logger.info("Found {} transformers for {}", transformers.size(), transformedName);

        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, ClassReader.EXPAND_FRAMES);

        for (ITransformer transformer : transformers) {
            logger.info("Applying transformer {} on {}...", transformer.getClass().getName(), transformedName);
            transformer.transform(node, transformedName);
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        try {
            node.accept(writer);
        } catch (Throwable t) {
            logger.error("Exception when transforming " + transformedName + ": " + t.getClass().getSimpleName());
            t.printStackTrace();
        }

        if (debug) {
            List<String> debuggableMethods = new ArrayList<>();
            for (ITransformer transformer : transformers) {
                debuggableMethods.addAll(transformer.debuggableClass());
            }

            if (debuggableMethods.size() > 0) {
                logger.info("Debugger is visiting class "+node.name+"...");
                try {
                    ClassVisitor visitor = new CheckClassAdapter(writer);
                    visitor.visit(node.version, node.access, node.name, node.signature, node.superName, node.interfaces.toArray(new String[0]));
                    for (MethodNode method : node.methods) {
                        if (debuggableMethods.contains(method.name)) {
                            logger.info("Debugger is visiting method " + node.name + "#" + method.name + "...");
                            try {
                                MethodVisitor methodVisitor = visitor.visitMethod(method.access, method.name, method.desc, method.signature, method.exceptions.toArray(new String[0]));
                                methodVisitor.visitCode();
                                methodVisitor.visitEnd();
                            } catch (Throwable e) {
                                logger.error("Error while visiting method "+ node.name + "#" + method.name, e);
                            }
                            logger.info("Method " + node.name + "#" + method.name + " has been visited by the debugger.");
                        }
                    }
                } catch (Throwable e) {
                    logger.error("Error while visiting class "+node.name, e);
                }
                logger.info("Class "+node.name+" has been visited by the debugger.");
            }
        }

        return writer.toByteArray();
    }
}
