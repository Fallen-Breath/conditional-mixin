package me.fallenbreath.conditionalmixin.impl;

import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.util.Annotations;

import java.util.List;

public class AnnotationCleaner
{
	@Nullable
	private AnnotationNode previousRestrictionAnnotation;

	public void onPreApply(ClassNode targetClass)
	{
		this.previousRestrictionAnnotation = Annotations.getVisible(targetClass, Restriction.class);
	}

	public void onPostApply(ClassNode targetClass)
	{
		String descriptor = Type.getDescriptor(Restriction.class);
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
