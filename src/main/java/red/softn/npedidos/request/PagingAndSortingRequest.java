package red.softn.npedidos.request;

import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.LinkedList;
import java.util.List;

@Data
public class PagingAndSortingRequest {
    
    private Paging paging;
    
    private List<Sorting> sorting;
    
    public PagingAndSortingRequest() {
        this.paging = new Paging();
        this.sorting = new LinkedList<>();
    }
    
    @Data
    public static class Paging {
        
        private int page;
        
        private int size;
        
    }
    
    @Data
    public static class Sorting {
        
        private String property;
        
        private Sort.Direction direction;
        
    }
    
}
