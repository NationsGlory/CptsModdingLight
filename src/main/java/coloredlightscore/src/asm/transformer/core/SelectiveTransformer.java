package coloredlightscore.src.asm.transformer.core;

import com.google.common.base.Throwables;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.logging.Level;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

/**
 * The SelectiveTransformer.class was based on code written by diesieben07,
 * who has given express permission for its use in our code.
 * <p>
 * diesieben07's code had not been classified under the GPL license at the time
 * before we had obtained a copy.
 * <p>
 * Source: https://github.com/diesieben07/SevenCommons/tree/master/src/main/java/de/take_weiland/mods/commons
 */

public abstract class SelectiveTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes != null && transforms(transformedName)) {
            CLLog.log(Level.INFO, "Class " + transformedName + " is a candidate for transforming");

            try {
                ClassNode clazz = ASMUtils.getClassNode(bytes);
                if (transform(clazz, transformedName)) {
                    CLLog.info("Finished Transforming class " + transformedName);
                    ClassWriter writer = new ExtendedClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                    clazz.accept(writer);
                    bytes = writer.toByteArray();
                } else
                    CLLog.log(Level.WARNING, "Did not transform {}", transformedName);
            } catch (Exception e) {
                CLLog.log(Level.SEVERE, "Exception during transformation of class " + transformedName);
                e.printStackTrace();
                Throwables.propagate(e);
            }
        }
        return bytes;
    }

    protected abstract boolean transforms(String className);

    protected abstract boolean transform(ClassNode clazz, String className);
}