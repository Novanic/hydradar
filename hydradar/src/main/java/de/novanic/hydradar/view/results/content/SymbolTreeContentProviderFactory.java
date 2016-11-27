package de.novanic.hydradar.view.results.content;

import de.novanic.hydradar.io.data.ResultData;
import de.novanic.hydradar.io.data.symbol.MethodSymbol;
import de.novanic.hydradar.io.data.symbol.PackageSymbol;
import de.novanic.hydradar.io.data.symbol.TypeSymbol;
import de.novanic.hydradar.io.data.symbol.VariableSymbol;
import org.eclipse.jdt.core.IType;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author sstrohschein
 *         <br>Date: 27.11.2016
 *         <br>Time: 13:38
 */
public final class SymbolTreeContentProviderFactory
{
    private static class SymbolTreeContentProviderFactoryHolder {
        private static final SymbolTreeContentProviderFactory myInstance = new SymbolTreeContentProviderFactory();
    }

    public static SymbolTreeContentProviderFactory getInstance() {
        return SymbolTreeContentProviderFactoryHolder.myInstance;
    }

    public SymbolTreeContentProvider createSymbolTreeContentProvider(ResultData aResultData, boolean isShowCurrentType, IType aCurrentType, boolean isShowSystemGroup) {
        final SymbolTreeContentProvider theContentProvider;
        if(isShowCurrentType) {
            theContentProvider = createUnusedSymbolsOfActiveTypeContentProvider(aResultData, aCurrentType);
        } else if(isShowSystemGroup) {
            theContentProvider = createUnusedSymbolsGroupedBySystemModuleContentProvider(aResultData);
        } else {
            theContentProvider = createUnusedSymbolsContentProvider(aResultData);
        }
        return theContentProvider;
    }

    public SymbolTreeContentProvider createEmptyContentProvider() {
        return new TreeContentProviderUngrouped();
    }

    private SymbolTreeContentProvider createUnusedSymbolsOfActiveTypeContentProvider(ResultData aResultData, IType aCurrentType) {
        if(aCurrentType != null) {
            String theTypeName = aCurrentType.getFullyQualifiedName();
            return createUnusedSymbolsOfTypeContentProvider(theTypeName, aResultData);
        }
        return createEmptyContentProvider();
    }

    private SymbolTreeContentProvider createUnusedSymbolsOfTypeContentProvider(String aTypeName, ResultData aResultData) {
        SortedSet<PackageSymbol> theUnusedPackages = new TreeSet<>();
        SortedSet<TypeSymbol> theUnusedTypes = new TreeSet<>();
        SortedSet<MethodSymbol> theUnusedMethods = new TreeSet<>();
        SortedSet<MethodSymbol> theUselessMethods = new TreeSet<>();
        SortedSet<VariableSymbol> theUnusedVariables = new TreeSet<>();

        int thePackageEndPos = aTypeName.lastIndexOf('.');
        if(thePackageEndPos >= 0) {
            String thePackage = aTypeName.substring(0, thePackageEndPos + 1);
            for(PackageSymbol theUnusedPackage: aResultData.getUnusedPackages()) {
                if(thePackage.startsWith(theUnusedPackage.getSymbolName())) {
                    theUnusedPackages.add(theUnusedPackage);
                }
            }
        }
        for(TypeSymbol theUnusedType: aResultData.getUnusedTypes()) {
            if(aTypeName.equals(theUnusedType.getEnclosingTypeName())) {
                theUnusedTypes.add(theUnusedType);
            }
        }
        for(MethodSymbol theUnusedMethod: aResultData.getUnusedMethods()) {
            if(aTypeName.equals(theUnusedMethod.getTypeName())) {
                theUnusedMethods.add(theUnusedMethod);
            }
        }
        for(MethodSymbol theUselessMethod: aResultData.getUselessMethods()) {
            if(aTypeName.equals(theUselessMethod.getTypeName())) {
                theUselessMethods.add(theUselessMethod);
            }
        }
        for(VariableSymbol theUnusedVariable: aResultData.getUnusedVariables()) {
            if(aTypeName.equals(theUnusedVariable.getTypeName())) {
                theUnusedVariables.add(theUnusedVariable);
            }
        }

        return new TreeContentProviderUngrouped(theUnusedPackages, theUnusedTypes, theUnusedMethods, theUselessMethods, theUnusedVariables);
    }

    private SymbolTreeContentProvider createUnusedSymbolsGroupedBySystemModuleContentProvider(ResultData aResultData) {
        return new TreeContentProviderGrouped(aResultData);
    }

    private SymbolTreeContentProvider createUnusedSymbolsContentProvider(ResultData aResultData) {
        return new TreeContentProviderUngrouped(aResultData.getUnusedPackages(),
                aResultData.getUnusedTypes(),
                aResultData.getUnusedMethods(),
                aResultData.getUselessMethods(),
                aResultData.getUnusedVariables());
    }
}