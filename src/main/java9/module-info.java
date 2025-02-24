/**
 * Feature flag system.
 */
@org.jspecify.annotations.NullMarked
module net.kyori.option {
  requires static transitive org.jspecify;
  requires static transitive org.jetbrains.annotations;

  exports net.kyori.option;
  exports net.kyori.option.value;
}
