package com.itemReaderBatch.config;

import com.itemReaderBatch.entity.StudentCsv;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import com.itemReaderBatch.writer.FirstItemWriter;
import java.io.File;

@Configuration
public class BatchConfig {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    FirstItemWriter firstItemWriter;
    @Bean
    public Job job(){
        return jobBuilderFactory.get("Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(firstChunkStep())
                .build();
    }

    private Step firstChunkStep(){
        return stepBuilderFactory.get("First Chunk Step")
                .<StudentCsv,StudentCsv>chunk(1)
                .reader(flatFileItemReader())
                .writer(firstItemWriter)
                .build();
    }

    public FlatFileItemReader<StudentCsv> flatFileItemReader(){
        FlatFileItemReader<StudentCsv> flatFileItemReader=new FlatFileItemReader<StudentCsv>();
        flatFileItemReader.setResource(new FileSystemResource((new File("/Users/luisguillermocruzvargas/Documents/java_projects/itemReaderBatch/src/main/resources/StudentsCsv.csv"))));
        flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>(){
            {
                setLineTokenizer(new DelimitedLineTokenizer(){
                    {
                        setNames("ID","First Name","Last Name","Email");
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>(){{
                    setTargetType(StudentCsv.class);
                }});
            }
        });
        flatFileItemReader.setLinesToSkip(1);
        System.out.println(flatFileItemReader);
        return flatFileItemReader;
    }

}
