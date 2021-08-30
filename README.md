# Fabric BiomeApi Reforged

Fabric Biome Api Reforged is a port of Fabric biomes api to forge. As for why I did this, instead of using forge's api? Well because Forge's biome api sucks! Especially if you want to do anything more then add a biome to the world, like make a varient of an existing biome or make a biome only spawn as a continent or river or island, ect. 

You can find the Original project <a href=https://github.com/FabricMC/fabric/tree/1.17/fabric-biome-api-v1>Here</a>.


### Usage

To add a Terraform module as a dependency simply add the terraformers' maven as a repository for dependencies. This can be done as follows:

```java
repositories {
    maven {
      name = 'Github'
      url = "https://jitpack.io"
    }
}
```

Make sure to put that maven after any other mavens you have in repositories

Then add the corresponding Terraform module as a dependency. For example, the following line adds the wood api as a dependency.

#### Forge Loom:
```java
dependencies {
    modImplementation "com.github.Reforged-Mods:FabricBiomeApiReforged:<tag>"
}
```

#### Forge Gradle:
```java
dependencies {
    implementation fg.deobf("com.github.Reforged-Mods:FabricBiomeApiReforged:<tag>")
}
```

Make sure to replace <tag> with one of these 3: a specific commit, main-SNAPSHOT(if you want the latest changes), or a release version, like v1.0.2.
Also if it times out, that just means it needs to build the build, just wait a couple minutes and try again.

Note: this will not work with fabric and never will, there is no need for it to do so, since you can just use the fabric api.
