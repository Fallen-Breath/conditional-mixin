package me.fallenbreath.conditionalmixin.api.mixin;

import me.fallenbreath.conditionalmixin.impl.AnnotationCleanerImpl;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.util.Annotations;

import java.lang.annotation.Annotation;

/**
 * Removed annotation merged to the target class from mixin class
 * Mixin excludes some of its annotation class in {@link Annotations#isMergeableAnnotation}, but we can't mixin into that
 * and added our own classes into the exclusion list, so here's what this class is made for
 * <p>
 * What {@link AnnotationCleaner} do:
 * 1. onPreApply: Records the existing annotation on the target class (if that exists)
 * 2. Mixin applies the mixin merge
 * 3. onPostApply: Reverts the annotation on the target class back to the state before apply
 */
public interface AnnotationCleaner
{
	static AnnotationCleaner create(Class<? extends Annotation> annotationClass)
	{
		return new AnnotationCleanerImpl(annotationClass);
	}

	/**
	 * Invoke me in {@link org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin#preApply}
	 */
	void onPreApply(ClassNode targetClass);

	/**
	 * Invoke me in {@link org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin#postApply}
	 */
	void onPostApply(ClassNode targetClass);
}
