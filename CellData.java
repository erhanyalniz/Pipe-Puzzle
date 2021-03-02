//Erhan Yaln�z   150117905
//Erdem A�ca     150117043
//The game consists of 5 different levels. The purpose of this game is to connect the pipes to provide that the ball moves.
// The aim of this class is to carry the required values of the cells.

class CellData{
    //Each id number dedicated to cells(Pipes and etc.).
    public int id;
    //Each type for cells. Ex: Pipe,PipeStatic,Empty,etc.
    public String type;
    //Each property for cells. Ex: 00,01,10,11,Vertical,Horizontal,Free,none,etc.
    public String property;

    //Constructor for cells.
    public CellData(int id, String type, String property){
        this.id = id;
        this.type = type;
        this.property = property;
    }
}