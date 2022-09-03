package com.stefan.springjwt.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.http.MediaType;


import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.io.File;
import java.io.FilenameFilter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.stefan.springjwt.models.Shoelast;
import com.stefan.springjwt.models.Shoelastenc;
import com.stefan.springjwt.models.Comment;
import com.stefan.springjwt.repository.ShoelastRepository;
import com.stefan.springjwt.repository.CommentRepository;
import org.springframework.data.repository.query.Param;



import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.stefan.springjwt.payload.response.MessageResponse;
import com.stefan.springjwt.models.FileInfo;
import com.stefan.springjwt.uploadservice.FilesStorageService;

import org.springframework.transaction.annotation.Transactional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
  @Autowired
  ShoelastRepository shoelastRepository;
  
  @Autowired
  CommentRepository commentRepository;

  @Autowired 
  HttpServletRequest request;

  @Autowired
  FilesStorageService storageService;

  
  private Sort.Direction getSortDirection(String direction) {
    if (direction.equals("asc")) {
      return Sort.Direction.ASC;
    } else if (direction.equals("desc")) {
      return Sort.Direction.DESC;
    }

    return Sort.Direction.ASC;
  }

  // @GetMapping("/sortedshoelasts")
  // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  // public ResponseEntity<List<Shoelast>> getAllShoelasts(@RequestParam(defaultValue = "id,desc") String[] sort) {

  //   try {
  //     List<Order> orders = new ArrayList<Order>();

  //     if (sort[0].contains(",")) {
  //       // will sort more than 2 fields
  //       // sortOrder="field, direction"
  //       for (String sortOrder : sort) {
  //         String[] _sort = sortOrder.split(",");
  //         orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
  //       }
  //     } else {
  //       // sort=[field, direction]
  //       orders.add(new Order(getSortDirection(sort[1]), sort[0]));
  //     }

  //     List<Shoelast> shoelasts = shoelastRepository.findAll(Sort.by(orders));

  //     if (shoelasts.isEmpty()) {
  //       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  //     }

  //     return new ResponseEntity<>(shoelasts, HttpStatus.OK);
  //   } catch (Exception e) {
  //     return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
  //   }
  // }

  @GetMapping("/shoelasts")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  public ResponseEntity<Map<String, Object>> getAllShoelastsPage(
      @RequestParam(defaultValue = "-1") int shoesize,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "3") int size,
      @RequestParam(defaultValue = "id,desc") String[] sort) {

    try {
      List<Order> orders = new ArrayList<Order>();

      if (sort[0].contains(",")) {
        // will sort more than 2 fields
        // sortOrder="field, direction"
        for (String sortOrder : sort) {
          String[] _sort = sortOrder.split(",");
          orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
        }
      } else {
        // sort=[field, direction]
        orders.add(new Order(getSortDirection(sort[1]), sort[0]));
      }

      List<Shoelast> shoelasts = new ArrayList<Shoelast>();
      Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

      Page<Shoelast> pageTuts;
      if (shoesize == -1)
        pageTuts = shoelastRepository.findAll(pagingSort);
      else
        pageTuts = shoelastRepository.findByShoesize(shoesize, pagingSort);

      shoelasts = pageTuts.getContent();

      Map<String, Object> response = new HashMap<>();
      response.put("shoelasts", shoelasts);
      response.put("currentPage", pageTuts.getNumber());
      response.put("totalItems", pageTuts.getTotalElements());
      response.put("totalPages", pageTuts.getTotalPages());

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @GetMapping("/shoelasts/{id}")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  public ResponseEntity<Shoelast> getShoelastById(@PathVariable("id") long id) {
    Optional<Shoelast> shoelastData = shoelastRepository.findById(id);

    if (shoelastData.isPresent()) {
      return new ResponseEntity<>(shoelastData.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  // @PostMapping("/shoelasts")
  // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")

  @RequestMapping(path = "/shoelasts", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  public ResponseEntity<MessageResponse> createShoelast(@RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("shoesize") int shoesize, @RequestPart("file") MultipartFile file) {

    // Shoelast shoelast = shoelastenc.shoelast;
    // MultipartFile file = shoelastenc.file;

    Shoelast shoelast = new Shoelast(title, description, shoesize);
    
    String filename = file.getOriginalFilename();
    Optional<String> extension =  Optional.ofNullable(filename)
      .filter(f -> f.contains("."))
      .map(f -> f.substring(filename.lastIndexOf(".") + 1));


    if (extension.isPresent() == false) {
        String message = "extension not persist";
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new MessageResponse(message));
    } else {
      String message = "";
      try {
        Shoelast _shoelast = shoelastRepository.save(new Shoelast(shoelast.getTitle(), shoelast.getDescription(), shoelast.getShoesize()));
        
        storageService.save(file, ""+_shoelast.getId() + "." + extension.get());

        message = "Adding success " + file.getOriginalFilename();
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(message));
      }  catch (Exception e) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    

  }

  @PutMapping("/shoelasts/{id}")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  public ResponseEntity<Shoelast> updateShoelast(@PathVariable("id") long id, @RequestBody Shoelast shoelast) {
    Optional<Shoelast> shoelastData = shoelastRepository.findById(id);

    if (shoelastData.isPresent()) {
      Shoelast _shoelast = shoelastData.get();
      _shoelast.setTitle(shoelast.getTitle());
      _shoelast.setDescription(shoelast.getDescription());
      _shoelast.setShoesize(shoelast.getShoesize());
      return new ResponseEntity<>(shoelastRepository.save(_shoelast), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Transactional
  @DeleteMapping("/shoelasts/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<MessageResponse> deleteShoelast(@PathVariable("id") long id) {
    

    Optional<Shoelast> shoelastData = shoelastRepository.findById(id);

    if (shoelastData.isPresent() == false) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    try {
      
      Shoelast sh =  shoelastData.get();

      String filePrefix = ""+id;
      File f = new File(com.stefan.springjwt.uploadservice.FilesStorageServiceImpl.uploadFolderPath);
      File[] matchingFiles = f.listFiles(new FilenameFilter() {
          public boolean accept(File dir, String name) {
              return name.startsWith(filePrefix);
          }
      });

      shoelastRepository.deleteById(sh.getId());
      if (matchingFiles.length != 1) {
        shoelastRepository.save(sh);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("!= 1"));
      }


      File targetFile = matchingFiles[0];
      storageService.deleteFile(targetFile.getName());

      
      commentRepository.deleteByTutid(id);
      // return new ResponseEntity<>(HttpStatus.OK);
      return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("radi"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(e.getMessage()));
    }
  }


  @DeleteMapping("/shoelasts")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<HttpStatus> deleteAllShoelasts() {
    try {
      shoelastRepository.deleteAll();
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }


  @GetMapping("/all")
  public String allAccess() {
    //return "Sistem za predstavljanje obućarskih kalupa.";
    return "Shoelasts catalog system.";
  }

  // @GetMapping("/user")
  // @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  // public String userAccess() {
  //   return "User Content.";
  // }

  // @GetMapping("/mod")
  // @PreAuthorize("hasRole('MODERATOR')")
  // public String moderatorAccess() {
  //   return "Moderator Board.";
  // }

  // @GetMapping("/admin")
  // @PreAuthorize("hasRole('ADMIN')")
  // public String adminAccess() {

  //   if (request.isUserInRole("ADMIN")) {
  //     return "Admin Board. 3122222";
  //   }
  //   return "Admin Board. 312";
  // }

  

  @GetMapping("/comments")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  public ResponseEntity<Map<String, Object>> getAllCommentsPage(
      @RequestParam(required = true) Long tutid,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "3") int size,
      @RequestParam(defaultValue = "id,desc") String[] sort) {

    try {
      List<Order> orders = new ArrayList<Order>();

      if (sort[0].contains(",")) {
        // will sort more than 2 fields
        // sortOrder="field, direction"
        for (String sortOrder : sort) {
          String[] _sort = sortOrder.split(",");
          orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
        }
      } else {
        // sort=[field, direction]
        orders.add(new Order(getSortDirection(sort[1]), sort[0]));
      }

      List<Comment> comments = new ArrayList<Comment>();
      Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

      Page<Comment> pageTuts;
      pageTuts = commentRepository.findByTutid(tutid, pagingSort);

      comments = pageTuts.getContent();

      Map<String, Object> response = new HashMap<>();
      response.put("comments", comments);
      response.put("currentPage", pageTuts.getNumber());
      response.put("totalItems", pageTuts.getTotalElements());
      response.put("totalPages", pageTuts.getTotalPages());

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @PostMapping("/comments")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
    try {
      Comment _comment = commentRepository.save(new Comment(comment.getTutid(), comment.getDescription()));
      return new ResponseEntity<>(_comment, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @DeleteMapping("/comments/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<HttpStatus> deleteComments(@PathVariable("id") long id) {
    try {
      commentRepository.deleteById(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // public ResponseEntity<Resource> getFile(String filename) {
  //   Resource file = storageService.load(filename);
  //   return ResponseEntity.ok()
  //       .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  // }

  // @GetMapping("/desibre")
  // public ResponseEntity<MessageResponse> desibre(@RequestParam(name = "file", required = true) Long file){
  //  // throws IOException {
        
  //     // String fileExt = ""+fileId;

  //     // File f = new File(com.stefan.springjwt.uploadservice.FilesStorageServiceImpl.uploadFolderPath);
  //     // File[] matchingFiles = f.listFiles(new FilenameFilter() {
  //     //     public boolean accept(File dir, String name) {
  //     //         return name.startsWith(fileExt);
  //     //     }
  //     // });

  //     // if (matchingFiles.length != 1) {
  //     //   return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
  //     // }
        
  //     // String fileName = com.stefan.springjwt.uploadservice.FilesStorageServiceImpl.uploadFolderPath + matchingFiles[0].getName();

  //     // FileUploadResponse response = new FileUploadResponse();
  //     // response.setFileName(fileName);
  //     // response.setSize(size);
  //     // response.setDownloadUri("/downloadFile/" + filecode);
        
  //     return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new MessageResponse("fileName"));
  //     //return new ResponseEntity<>(new MessageResponse("fileName"), HttpStatus.OK);
  // }


  // @GetMapping("/getimage/{id}")
  // public ResponseEntity<HttpStatus> getImage(@PathVariable("id") long id) {
  //   return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  // }

  @GetMapping("/getimage/{id}")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  public ResponseEntity<Resource> getFile(@PathVariable("id") long id) {

    
      String fileStarts = ""+id;

      File f = new File(com.stefan.springjwt.uploadservice.FilesStorageServiceImpl.uploadFolderPath);
      File[] matchingFiles = f.listFiles(new FilenameFilter() {
          public boolean accept(File dir, String name) {
              return name.startsWith(fileStarts);
          }
      });

      if (matchingFiles.length != 1) {
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
      }
        
    String fileName =matchingFiles[0].getName();

    Resource file = storageService.load(fileName);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }
  
}
