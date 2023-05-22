package coloredlightscore.src.helper;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.logging.Level;

import static cpw.mods.fml.common.ObfuscationReflectionHelper.remapFieldNames;

public class CLReflectionHelper {

    public static <T, E> void setPrivateFinaleValue(Class<? super T> classToAccess, T instance, E value, String... fieldNames) {
        try {
            Field field = ReflectionHelper.findField(classToAccess, remapFieldNames(classToAccess.getName(), fieldNames));

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(instance, value);

        } catch (ReflectionHelper.UnableToFindFieldException e) {
            FMLLog.log(Level.SEVERE, e, "Unable to locate any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName());
            throw e;
        } catch (ReflectionHelper.UnableToAccessFieldException e) {
            FMLLog.log(Level.SEVERE, e, "Unable to set any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName());
            throw e;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
