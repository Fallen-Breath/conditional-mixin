package me.fallenbreath.conditionalmixin.impl;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.util.Annotations;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Removed annotation merged to the target class from mixin class
 * Mixin excludes some of its annotation class in {@link Annotations#isMergeableAnnotation}, but we can't mixin into that
 * and added our own classes into the exclusion list, so here's what this class is made for
 *
 * What this class do:
 * 1. onPreApply: Records the existing annotation on the target class (if that exists)
 * 2. Mixin applies the mixin merge
 * 3. onPostApply: Reverts the annotation on the target class back to the state before apply
 */
public class AnnotationCleaner
{
	private final Class<? extends Annotation> annotationClass;

	public AnnotationCleaner(Class<? extends Annotation> annotationClass)
	{
		this.annotationClass = annotationClass;
	}

	@Nullable
	private AnnotationNode previousRestrictionAnnotation;

	public void onPreApply(ClassNode targetClass)
	{
		this.previousRestrictionAnnotation = Annotations.getVisible(targetClass, this.annotationClass);
	}

	public void onPostApply(ClassNode targetClass)
	{
		String descriptor = Type.getDescriptor(this.annotationClass);
		List<AnnotationNode> annotationNodes = targetClass.visibleAnnotations;
		if (annotationNodes == null)
		{
			return;
		}
		int index = -1;
		for (int i = 0; i < annotationNodes.size(); i++)
		{
			if (descriptor.equals(annotationNodes.get(i).desc))
			{
				index = i;
				break;
			}
		}
		if (this.previousRestrictionAnnotation == null && index != -1)  // @Restriction is merged, drop it
		{
			annotationNodes.remove(index);
		}
		else if (this.previousRestrictionAnnotation != null)  // preserve the original @Restriction
		{
			annotationNodes.set(index, this.previousRestrictionAnnotation);
		}
	}
}
