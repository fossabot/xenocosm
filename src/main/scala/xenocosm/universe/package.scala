package xenocosm

package object universe {
  object instances extends data.Galaxy.Instances
                      with data.HubbleSequence.Instances
                      with data.MorganKeenan.Instances
                      with data.Star.Instances
                      with data.Universe.Instances
}
